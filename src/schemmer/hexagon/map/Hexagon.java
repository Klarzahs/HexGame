package schemmer.hexagon.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import schemmer.hexagon.biomes.Biome;
import schemmer.hexagon.biomes.Desert;
import schemmer.hexagon.biomes.Forest;
import schemmer.hexagon.biomes.GrassDesert;
import schemmer.hexagon.biomes.RainForest;
import schemmer.hexagon.biomes.Savanna;
import schemmer.hexagon.biomes.SeasonalForest;
import schemmer.hexagon.biomes.Swamp;
import schemmer.hexagon.biomes.Taiga;
import schemmer.hexagon.biomes.Tundra;
import schemmer.hexagon.buildings.Barracks;
import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Farm;
import schemmer.hexagon.buildings.Forge;
import schemmer.hexagon.buildings.Hut;
import schemmer.hexagon.buildings.Lumbermill;
import schemmer.hexagon.buildings.Quarry;
import schemmer.hexagon.buildings.Stable;
import schemmer.hexagon.buildings.TownCenter;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.game.Screen;
import schemmer.hexagon.loader.Image;
import schemmer.hexagon.loader.ImageLoader;
import schemmer.hexagon.loader.ImageNumber;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.units.UnitState;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Log;
import schemmer.hexagon.utils.Point;

public class Hexagon {
	@ImageNumber(number = 32)	// 2 are non existent in images[]
	private static BufferedImage[] additions = new BufferedImage[12];
	private static BufferedImage[] images = new BufferedImage[22];
	
	public final static double CORNERS = 6;	// # Corners
	private static int SIZE = 30;			// in Pixel
	private static float widthFactor = (float) (Math.sqrt(3)/2f);
	
	private HexType type;
	private Biome biome;
	
	private Point center;		// pixel
	private Cube coords;		// cube coords
	private int posX, posY;		// coords in map
	
	private BufferedImage image;
	private BufferedImage addition;
	
	private Unit unit;
	private Building building;
	
	private Main main;
	
	
	// sorting
	public int priority = 999;
	private int costs = -1;
	
	public Hexagon(Main m, Cube c, int x, int y){
		this.coords = c;
		this.center = Conv.cubeToPixel(c);
		center = new Point(center.x + Screen.WIDTH/2, center.y + Screen.HEIGHT/2);
		this.posX = x;
		this.posY = y;
		main = m;
	}
	
	
	@Image
	public static void loadImages(GraphicsConfiguration gc){
		if(gc != null){
			images[0] = ImageLoader.loadImage("/png/images/tileAutumn.png");
			//images[1] = ImageLoader.loadImage("/png/images/tileDeepwater.png");  -- non existent
			images[2] = ImageLoader.loadImage("/png/images/tileDirt.png");
			images[3] = ImageLoader.loadImage("/png/images/tileGrass.png");
			images[4] = ImageLoader.loadImage("/png/images/tileLava.png");
			images[5] = ImageLoader.loadImage("/png/images/tileMagic.png");
			images[6] = ImageLoader.loadImage("/png/images/tileRock.png");
			images[7] = ImageLoader.loadImage("/png/images/tileSand.png");
			images[8] = ImageLoader.loadImage("/png/images/tileSnow.png");
			images[9] = ImageLoader.loadImage("/png/images/tileStone.png");
			images[10] = ImageLoader.loadImage("/png/images/tileWater.png");
			images[11] = ImageLoader.loadImage("/png/images/tileAutumn_tile.png");
			images[12] = ImageLoader.loadImage("/png/images/tileDeepwater_tile.png");
			images[13] = ImageLoader.loadImage("/png/images/tileDirt_tile.png");
			images[14] = ImageLoader.loadImage("/png/images/tileGrass_tile.png");
			//images[15] = ImageLoader.loadImage("/png/images/tileLava_tile.png");  -- non existent
			images[16] = ImageLoader.loadImage("/png/images/tileMagic_tile.png");
			images[17] = ImageLoader.loadImage("/png/images/tileRock_tile.png");
			images[18] = ImageLoader.loadImage("/png/images/tileSand_tile.png");
			images[19] = ImageLoader.loadImage("/png/images/tileSnow_tile.png");
			images[20] = ImageLoader.loadImage("/png/images/tileStone_tile.png");
			images[21] = ImageLoader.loadImage("/png/images/tileWater_tile.png");
			
			additions[0] = ImageLoader.loadImage("/png/additions/addDeepwater.png");
			additions[1] = ImageLoader.loadImage("/png/additions/addDesert.png");
			additions[2] = ImageLoader.loadImage("/png/additions/addForest.png");
			additions[3] = ImageLoader.loadImage("/png/additions/addGrassdesert.png");
			additions[4] = ImageLoader.loadImage("/png/additions/addRainforest.png");
			additions[5] = ImageLoader.loadImage("/png/additions/addSavanna.png");
			additions[6] = ImageLoader.loadImage("/png/additions/addSeasonalforest.png");
			additions[7] = ImageLoader.loadImage("/png/additions/addSwamp.png");
			additions[8] = ImageLoader.loadImage("/png/additions/addTaiga.png");
			additions[9] = ImageLoader.loadImage("/png/additions/addTundra.png");
			additions[10] = ImageLoader.loadImage("/png/additions/addWater.png");
			additions[11] = ImageLoader.loadImage("/png/additions/addWRocksnow.png");
		}
	}
	
	public Point hexCorner(double i){
		double angleDeg = 360/CORNERS * i + 30;
		double angleRad = Math.PI /180 * angleDeg;
		return new Point(center.x + SIZE * Math.cos(angleRad), center.y + SIZE * Math.sin(angleRad));
	}
	
	public void draw(Graphics2D g2d, int offX, int offY){
		recalculateCenter();
		g2d.setColor(type.getColor());
		for (int i = 0; i < CORNERS; i++){
			g2d.drawLine((int)(hexCorner(i).x)-offX, (int)(hexCorner(i).y)-offY, (int)(hexCorner((i+1)%CORNERS).x)-offX, (int)(hexCorner((i+1)%CORNERS).y)-offY);
		}
		g2d.drawString(""+(int)this.coords.getV()[0] + "|" + (int)this.coords.getV()[1]+"|"+(int)this.coords.getV()[2], (int)center.x-offX, (int)center.y-offY);
	}
	
	public void drawPicture(Graphics2D g2d, int offX, int offY){
		recalculateCenter();
		float OSF = SIZE/50f; 			//Offset Scaling Factor
		
		// Base image
		if(image == null)
			createPicture();
		g2d.drawImage(image, (int) (center.x-offX-SIZE+7*OSF), (int)center.y-offY-SIZE, (int) (SIZE*Math.sqrt(3) +1*OSF), (int)(SIZE*2 + 1 * OSF), null);
		
		// Additions
		if(addition == null)
			createAddition();
		if(type.getIndex() == HexTypeInt.TYPE_MOUNTAIN.getValue() || type.getIndex() == HexTypeInt.TYPE_HILL.getValue())
			g2d.drawImage(addition, (int)(center.x-offX-SIZE + 15*OSF), (int)(center.y-offY-SIZE ), (int) (SIZE*Math.sqrt(3) - 15 * OSF), (int)(SIZE*2 - 15*OSF), null);
		else 
			g2d.drawImage(addition, (int) (center.x-offX-SIZE+7*OSF), (int)center.y-offY-SIZE, (int) (SIZE*Math.sqrt(3) +1*OSF), (int)(SIZE*2 + 1 * OSF), null);
		
		// Unit image
		if(unit != null)
			g2d.drawImage(unit.getImage(), (int)(center.x-offX-SIZE + 10*OSF), (int)(center.y-offY-SIZE  ), (int) (SIZE*Math.sqrt(3) - 10 * OSF), (int)(SIZE*2 - 5*OSF), null);
		
		// Building image
		if(building != null)
			g2d.drawImage(building.getImage(), (int)(center.x-offX-SIZE + 10*OSF), (int)(center.y-offY-SIZE  ), (int) (SIZE*Math.sqrt(3) - 10 * OSF), (int)(SIZE*2 - 5*OSF), null);
	}
	
	private void createPicture(){
		int nr = 0;
		if(biome != null){
			nr += biome.getImage();
			nr += type.getImage();
		} else{
			if (type.getIndex() == HexTypeInt.TYPE_WATER.getValue())
				nr = 21;
			if (type.getIndex() == HexTypeInt.TYPE_DEEPWATER.getValue())
				nr = 12;
			if (type.getIndex() == HexTypeInt.TYPE_MOUNTAIN.getValue())
				nr += 8;
		}
		try{
			image = images[nr];
		} catch (Exception e){
			System.out.println("Couldn't load picture \""+nr+"\"");
		}
	}
	
	private void createAddition(){
		int nr = 0;
		if(biome != null){
			nr += biome.getAddition();
		} else 
			nr += type.getAddition();
		try{
			addition = additions[nr];
		} catch (Exception e){
			System.out.println("Couldn't load picture \""+nr+"\"");
		}
	}
	
	public void fill(Graphics2D g2d, int offX, int offY){
		recalculateCenter();
		g2d.setColor(type.getColor());
		int xs[] = new int [(int) CORNERS];
		int ys[] = new int [(int) CORNERS];
		
		for (int i = 0; i < xs.length; i++){
			xs[i] = (int) hexCorner(i).x - offX;
			ys[i] = (int) hexCorner(i).y - offY;
		}
		
		g2d.fillPolygon(xs, ys, (int) CORNERS);
	}
	
	public void fill(Graphics2D g2d, int offX, int offY, Color c){
		recalculateCenter();
		g2d.setColor(c);
		int xs[] = new int [(int) CORNERS];
		int ys[] = new int [(int) CORNERS];
		
		for (int i = 0; i < xs.length; i++){
			xs[i] = (int) hexCorner(i).x - offX;
			ys[i] = (int) hexCorner(i).y - offY;
		}
		
		g2d.fillPolygon(xs, ys, (int) CORNERS);
	}
	
	public void showMovement(Graphics2D g2d, Color c, int offX, int offY){
		recalculateCenter();
		g2d.setColor(c);
		int xs[] = new int [(int) CORNERS];
		int ys[] = new int [(int) CORNERS];
		
		for (int i = 0; i < xs.length; i++){
			xs[i] = (int) hexCorner(i).x - offX;
			ys[i] = (int) hexCorner(i).y - offY;
		}
		
		g2d.fillPolygon(xs, ys, (int) CORNERS);
	}
	
	public void drawOutline(Graphics2D g2d, int offX, int offY){
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < CORNERS; i++){
			g2d.drawLine((int)(hexCorner(i).x)-offX, (int)(hexCorner(i).y)-offY, (int)(hexCorner((i+1)%CORNERS).x)-offX, (int)(hexCorner((i+1)%CORNERS).y)-offY);
		}
		if (biome != null){
			if(costs != -1)
				g2d.drawString(""+this.getCosts(), (int)center.x-offX-25, (int)center.y-offY);
		}
	}
	
	public static double getSize(){
		return SIZE;
	}
	
	public String printCoords(){
		return coords.printCube()+"\n";
	}
	
	public String printCenter(){
		return "Center @"+center.x+" | "+center.y+"\n";
	}
	
	public void draw(Graphics2D g2d, Color c, Stroke s, int offX, int offY){
		g2d.setColor(c);
		g2d.setStroke(s);
		recalculateCenter();
		for (int i = 0; i < CORNERS; i++){
			g2d.drawLine((int)(hexCorner(i).x)-offX, (int)(hexCorner(i).y)-offY, (int)(hexCorner((i+1)%CORNERS).x)-offX, (int)(hexCorner((i+1)%CORNERS).y)-offY);
		}
	}
	
	public Cube getCoords(){
		return coords;
	}
	
	public Point getCenter(){
		return center;
	}
	
	public void recalculateCenter(){
		this.center = Conv.cubeToPixel(this.coords);
		center = new Point(center.x + Screen.WIDTH/2, center.y + Screen.HEIGHT/2);
	}
	
	public static void zoomIn(){
		if (SIZE < 80) SIZE ++;
	}
	
	public static void zoomOut(){
		if (SIZE > 20) SIZE --;
	}
	
	public HexType getType(){
		return type;
	}
	
	public void setType(int i){
		type = new HexType(i);
	}
	
	public void setBiome(Biome b){
		biome = b;
	}
	
	public Biome getBiome(){
		return biome;
	}
	
	public static boolean moveUnitTo(Unit u, Hexagon to){
		if(to.getType().isMoveable()){
			to.moveTo(u);
			return true;
		}
		return false;
	}
	
	public void moveTo(Unit u){
		Hexagon before = u.getField();
		moveToLocal(u);
		
		// send info to server
		if(!Main.isLocal){
			main.getClient().moveTo(before, this);
		}
	}
	
	public void moveToLocal(Unit u){
		// move unit locally
		this.unit = u;
		u.setField(this);
		u.getPlayer().setRessourcesChanged(true);

		if(this.getBuilding() != null && this.getUnit() != null){
			if(this.getBuilding().gettTB() > 0 && this.getUnit().isBuilder()) 
				this.unit.setState(UnitState.STATE_BUILDING);
			else 
				this.unit.setState(UnitState.STATE_NONE);
		}else if (this.getUnit() != null){
			this.unit.setState(UnitState.STATE_NONE);
		}
	}
	
	public boolean isMoveable(){
		return type.isMoveable();
	}
	
	public boolean isOccupied(){
		return (unit != null);
	}
	
	public Unit getUnit(){
		return unit;
	}
	
	public void unitMoved(){
		unit = null;
	}
	
	public int getMovementCosts(){
		if(type.isMoveable()){
			if(type.getIndex() == HexTypeInt.TYPE_HILL.getValue())			//hills cost 1 more movement point
				return biome.getMovementCosts() + 1;
			return biome.getMovementCosts();
		}
		else
			return -1;
	}
	
	public boolean equals(Hexagon b){
		return this.getCoords().equals(b.getCoords());
	}
	
	public void setCosts(int i){
		costs = i;
	}
	
	public int getCosts(){
		return costs;
	}
	
	public void deleteUnit(){
		this.getUnit().getPlayer().deleteUnit(this.getUnit());
		this.unit = null;
	}
	
	public void build(Player p, int i){
		switch (i){					// "Farm", "Lumbermill", "Quarry",  "Hut", "Towncenter"
		case 0:				
			this.building = new Farm(main, this);
			break;
		case 1:
			this.building = new Lumbermill(main, this);
			break;
		case 2:
			this.building = new Quarry(main, this);
			break;
		case 3:
			this.building = new Hut(main, this);
			break;
		case 4:
			this.building = new TownCenter(main, this);
			break;
		case 5:
			this.building = new Forge(main, this);
			break;
		case 6:
			this.building = new Barracks(main, this);
			break;
		case 7:
			this.building = new Stable(main, this);
			break;
		default:
			this.building = new TownCenter(main, this);
		}
		
		if(this.unit != null){
			this.unit.setState(UnitState.STATE_BUILDING);
			p.addBuilding(this.building);
		}
	}
	
	public boolean isBuildUpon(){
		if(building != null){
			if(building.gettTB() > 0) return false;
			return true;
		}
		return false;
	}
	
	public int getX(){
		return posX;
	}
	
	public int getY(){
		return posY;
	}
	
	public boolean isOccupiedByBuilder(){
		return this.isOccupied() && unit.isBuilder() && unit.getPlayer() == main.getCurrentPlayer();
	}
	
	public Building getBuilding(){
		return building;
	}
	
	public Hexagon neighbour(int i){
		Cube c = this.coords;
		Cube cnew = c.neighbour(i);
		if(cnew != null)
			return main.getMH().getInArray(cnew);
		return null;
	}
	
	public char getAsChar(){
		byte b = 0;
		if(type != null && type.getIndex() == HexTypeInt.TYPE_HILL.getValue()) b = 20; 
		if(biome instanceof Desert) return (char) (0 + b);
		if(biome instanceof Forest) return (char) (1 + b);
		if(biome instanceof GrassDesert) return (char) (2 + b);
		if(biome instanceof RainForest) return (char) (3 + b);
		if(biome instanceof Savanna) return (char) (4 + b);
		if(biome instanceof SeasonalForest) return (char) (5 + b);
		if(biome instanceof Swamp) return (char) (6 + b);
		if(biome instanceof Taiga) return (char) (7 + b);
		if(biome instanceof Tundra) return (char) (8 + b);
		if(type.getIndex() == HexTypeInt.TYPE_DEEPWATER.getValue()) return (char) (9 + b);
		if(type.getIndex() == HexTypeInt.TYPE_WATER.getValue()) return (char) (10 + b);
		if(type.getIndex() == HexTypeInt.TYPE_MOUNTAIN.getValue()) return (char) (11 + b);
		return (char)-1;
	}
}

