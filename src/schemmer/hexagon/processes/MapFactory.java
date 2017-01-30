package schemmer.hexagon.processes;

import schemmer.hexagon.biomes.Desert;
import schemmer.hexagon.biomes.Forest;
import schemmer.hexagon.biomes.GrassDesert;
import schemmer.hexagon.biomes.RainForest;
import schemmer.hexagon.biomes.Savanna;
import schemmer.hexagon.biomes.SeasonalForest;
import schemmer.hexagon.biomes.Swamp;
import schemmer.hexagon.biomes.Taiga;
import schemmer.hexagon.biomes.Tundra;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Log;

public class MapFactory {
	private final static float deepWaterWeight = 0.285f;		
	private final static float waterWeight = 0.35f;
	private final static float fieldWeight = 0.70f;
	private final static float hillWeight = 0.80f;
	//private final static float mountainWeight = 1f; - implicit
	
	public static Tundra tundra = new Tundra();
	public static Desert desert = new Desert();
	public static Forest forest = new Forest();
	public static GrassDesert grassDesert = new GrassDesert();
	public static RainForest rainForest = new RainForest();
	public static Savanna savanna = new Savanna();
	public static Swamp swamp = new Swamp();
	public static Taiga taiga = new Taiga();
	public static SeasonalForest seasonalForest = new SeasonalForest();
	
	private static String SEED_MAP = "77223344";
	private static String SEED_BIOME = "98765432";
	
	private static float[][] noise;
	
	public static void createTypes(Main m, Hexagon[][] map, int radius){
		createMap(m, map, radius);
		
		// ------- Create terrain -----------
		SimplexNoise.updateGradients(SEED_MAP);
		noise = MapFactory.generateSimplexNoise(SimplexNoise.size*2, SimplexNoise.size*2, 0.006f);
		
		int pixelPerHex = (SimplexNoise.size*2) / (map.length);
		Log.d("ppH "+pixelPerHex+" SN: "+SimplexNoise.size+" length: "+map.length);
		for(int x = 0; x < map.length; x++){
			for (int y = 0; y < map[x].length; y++){
				if(map[x][y] != null){
					float hexNoise = hexToNoise(x, y, pixelPerHex);
					if(hexNoise < deepWaterWeight)
						map[x][y].setType(HexTypeInt.TYPE_DEEPWATER.getValue());
					else if(hexNoise < waterWeight)
						map[x][y].setType(HexTypeInt.TYPE_WATER.getValue());
					else if(hexNoise < fieldWeight)
						map[x][y].setType(HexTypeInt.TYPE_FIELD.getValue());
					else if(hexNoise < hillWeight)
						map[x][y].setType(HexTypeInt.TYPE_HILL.getValue());
					else 
						map[x][y].setType(HexTypeInt.TYPE_MOUNTAIN.getValue());
				}
			}
		}
		
		// ------- Create biomes -----------
		SimplexNoise.updateGradients(SEED_BIOME);
		noise = MapFactory.generateSimplexNoise(SimplexNoise.size*2, SimplexNoise.size*2, 0.05f);
		
		for(int x = 0; x < map.length; x++){
			for (int y = 0; y < map[x].length; y++){
				if(map[x][y] != null 
						&& map[x][y].getType().getIndex() != HexTypeInt.TYPE_DEEPWATER.getValue()
						&& map[x][y].getType().getIndex() != HexTypeInt.TYPE_WATER.getValue()
						&& map[x][y].getType().getIndex() != HexTypeInt.TYPE_MOUNTAIN.getValue() ){
					float rainfall = hexToNoise(x, y, pixelPerHex);
					//add coords to check if near aequator (hot)
					float nearAequator = (Math.abs(x-radius)) / ((float) radius);
					float temperature = 1 - nearAequator;
					if(rainfall < .35f){
						if(temperature < .15f)
							map[x][y].setBiome(tundra);
						else if(temperature < .45f)
							map[x][y].setBiome(seasonalForest);
						else if(temperature < .65f)
							map[x][y].setBiome(grassDesert);
						else
							map[x][y].setBiome(desert);
					}
					else if(rainfall < .5f){
						if(temperature < .2f)
							map[x][y].setBiome(tundra);
						else if(temperature < .30f)
							map[x][y].setBiome(taiga);
						else if(temperature < .65f)
							map[x][y].setBiome(forest);
						else
							map[x][y].setBiome(savanna);
					}
					else if(rainfall < .75f){
						if(temperature < .20f)
							map[x][y].setBiome(taiga);
						else if(temperature < .65f)
							map[x][y].setBiome(swamp);
						else
							map[x][y].setBiome(seasonalForest);
					}
					else
						map[x][y].setBiome(rainForest);
					
				}
			}
		}
	}
	
	//Simpson-rule integration at .25, .5, .75
	private static float hexToNoise(int x, int y, int pixelPerHex){
		float sumNoise = 0;
		x *= pixelPerHex;
		y *= pixelPerHex;
		sumNoise += 1*noise[(int)(x + 0.25f*pixelPerHex)][(int)(y + 0.25f*pixelPerHex)];
		sumNoise += 4*noise[(int)(x + 0.5f*pixelPerHex)][(int)(y + 0.5f*pixelPerHex)];
		sumNoise += 1*noise[(int)(x + 0.75f*pixelPerHex)][(int)(y + 0.75f*pixelPerHex)];
		sumNoise /= 6;
		return sumNoise;
	}
	
	private static void createMap(Main m, Hexagon[][] map, int radius){
		for (int q = -radius; q <= radius; q++) {
			int r1 = Math.max(-radius, -q - radius);
		    int r2 = Math.min(radius, -q + radius);
		    for (int r = r1; r <= r2; r++) {
		    	int x = r + radius;
		    	int y = q + radius + Math.min(0, r);
		    	map[x][y] = new Hexagon(m, new Cube(q, -q-r, r), x, y);
		    }
		}
		for(int x = 0; x <  map.length; x++){
			for (int y = 0; y < map[x].length; y++){
				System.out.print("["+x+", "+y+"],");
			}
			System.out.println("");
		}
	}
	
	public static float[][] generateSimplexNoise(int width, int height, float scale){
	    float[][] simplexnoise = new float[width][height];
	    
	    for(int x = 0; x < width; x++){
	       for(int y = 0; y < height; y++){
	          simplexnoise[x][y] = SimplexNoise.octavedNoise(x, y, scale); 	//(x * frequency,y * frequency);
	          simplexnoise[x][y] = (simplexnoise[x][y] + 1) / 2;   //generate values between 0 and 1
	       }
	    }
	    
	    return simplexnoise;
	}
	
	public static String getBiomeSeed(){
		return SEED_BIOME;
	}
	
	public static String getMapSeed(){
		return SEED_MAP;
	}
	
	public static void setBiomeSeed(String s){
		SEED_BIOME = s;
	}
	
	public static void setMapSeed(String s){
		SEED_MAP = s;
	}
}
