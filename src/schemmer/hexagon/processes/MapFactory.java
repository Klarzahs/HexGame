package schemmer.hexagon.processes;

import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.utils.Cube;

public class MapFactory {
	private final static float deepWaterWeight = 0.35f;		// forest and desert are determined by biome
	private final static float waterWeight = 0.45f;
	private final static float fieldWeight = 0.70f;
	private final static float hillWeight = 0.80f;
	private final static float mountainWeight = 1f;
	
	private static float[][] noise;
	
	public static void createTypes(Hexagon[][] map, int radius){
		createMap(map, radius);
		
		SimplexNoise.updateGradients();
		noise = MapFactory.generateSimplexNoise(SimplexNoise.size*2, SimplexNoise.size*2);
		
		int pixelPerHex = (SimplexNoise.size*2) / (map.length);
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
	
	private static void createMap(Hexagon[][] map, int radius){
		for (int q = -radius; q <= radius; q++) {
			int r1 = Math.max(-radius, -q - radius);
		    int r2 = Math.min(radius, -q + radius);
		    for (int r = r1; r <= r2; r++) {
		    	map[r + radius][q + radius + Math.min(0, r)] = new Hexagon(new Cube(q, -q-r, r));
		    	map[r + radius][q + radius + Math.min(0, r)].setType(5);
		    }
		}
	}
	
	public static float[][] generateSimplexNoise(int width, int height){
	    float[][] simplexnoise = new float[width][height];
	    
	    for(int x = 0; x < width; x++){
	       for(int y = 0; y < height; y++){
	          simplexnoise[x][y] = (float) SimplexNoise.octavedNoise(x, y); 	//(x * frequency,y * frequency);
	          simplexnoise[x][y] = (simplexnoise[x][y] + 1) / 2;   //generate values between 0 and 1
	       }
	    }
	    
	    return simplexnoise;
	}
	
}
