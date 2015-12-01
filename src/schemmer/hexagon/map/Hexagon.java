package schemmer.hexagon.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import schemmer.hexagon.biomes.Biome;
import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Farm;
import schemmer.hexagon.buildings.Hut;
import schemmer.hexagon.buildings.Lumbermill;
import schemmer.hexagon.buildings.TownCenter;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.game.Screen;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.units.UnitState;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Point;

public class Hexagon {
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
		String filepath = "/png/images/tile";
		if(biome != null){
			filepath += biome.getImage();
			filepath += type.getImage();
		} else{
			if (type.getIndex() == HexTypeInt.TYPE_WATER.getValue())
				filepath += "Water_tile";
			if (type.getIndex() == HexTypeInt.TYPE_DEEPWATER.getValue())
				filepath += "Deepwater_tile";
			if (type.getIndex() == HexTypeInt.TYPE_MOUNTAIN.getValue())
				filepath += "Snow";
		}
		filepath += ".png";
		try{
			image = ImageIO.read(this.getClass().getResourceAsStream(filepath));
		} catch (Exception e){
			System.out.println("Couldn't load picture \""+filepath+"\"");
		}
	}
	
	private void createAddition(){
		String filepath = "/png/additions/";
		if(biome != null){
			filepath += "add";
			filepath += biome.getAddition();
		} else 
			filepath += type.getAddition();
		filepath += ".png";
		try{
			addition = ImageIO.read(this.getClass().getResourceAsStream(filepath));
		} catch (Exception e){
			System.out.println("Couldn't load picture \""+filepath+"\"");
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
	
	public boolean moveTo(Hexagon hex){
		if(hex.getType().isMoveable()){
			hex.moveTo(this.unit);
			this.unit.moved(hex.getMovementCosts());
			this.unit = null;
			return true;
		}
		return false;
	}
	
	public static boolean moveUnitTo(Unit u, Hexagon to){
		if(to.getType().isMoveable()){
			to.moveTo(u);
			return true;
		}
		return false;
	}
	
	public void moveTo(Unit u){
		this.unit = u;
		u.setField(this);
		
		if(this.getBuilding() != null && this.getUnit() != null){
			if(this.getBuilding().gettTB() > 0 && this.getUnit().isBuilder()) 
				this.unit.setState(UnitState.STATE_BUILDING);
			else 
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
		switch (i){					// farm, lumbermill, hut, main building
		case 0:				
			this.building = new Farm(p, this);
			break;
		case 1:
			this.building = new Lumbermill(p, this);
			break;
		case 2:
			this.building = new Hut(p, this);
			break;
		case 3:
			this.building = new TownCenter(p, this);
			break;
		default:
			this.building = new TownCenter(p, this);
		}

		this.unit.setState(UnitState.STATE_BUILDING);
		p.addBuilding(this.building);
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
}

