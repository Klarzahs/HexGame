package schemmer.hexagon.handler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.game.Screen;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.server.Client;
import schemmer.hexagon.server.Server;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Dijkstra;
import schemmer.hexagon.utils.Log;
import schemmer.hexagon.utils.Point;

public class MapHandler {
	private Main main;
	private Screen screen;
	
	public int RADIUS = 25;
	public boolean DEBUG = false;
	
	private Hexagon[][] map;		//Range: 0..2*radius+1, but hex coords are range: -radius .. radius
	
	private Hexagon marked;
	private Hexagon hovered;
	private ArrayList<Hexagon> movementRange;
	private Point clicked;
	
	private final Color movementRangeColor = new Color(50, 50, 255, 75);
	private final Color movementRangeEnemyColor = new Color(255, 50, 255, 75);
	private final Color movementPathColor = new Color(255, 50, 50, 75);
	private final Color hoveredColor = new Color(150,150,250);
	private final Color markedColor = new Color(0,0,255);
	private final Color fogColor = new Color(175,175,175);
	
	private boolean mapLoaded = false;
	
	public MapHandler(Main main){
		this.main = main;
		createHexagon(RADIUS);
	}
	
	public MapHandler(Main main, Client client){
		this.main = main;
		map = client.getMapFromServer(main);
	}
	
	public void update(double delta){
		
	}
	
	public void draw(Graphics2D g2d, int offX, int offY){
		g2d.setColor(new Color(150,150,150));
		g2d.setStroke(new BasicStroke(1));
		
		//visibility map for current player
		boolean[][] visMap = null;
		if(main.getCurrentPlayer() != null)
			visMap = main.getCurrentPlayer().getVisMap();
		
		for (int q = map.length - 1 ; q >= 0 ; q--){		//draw backwards for overlapping images
			for (int r = map[q].length - 1; r >= 0; r--){
				if(map[q][r] != null){
					if(DEBUG){
						map[q][r].drawPicture(g2d, offX, offY);
						map[q][r].drawOutline(g2d, offX, offY);
					}else{
						if(visMap != null && visMap[q][r]){
							map[q][r].drawPicture(g2d, offX, offY);
							map[q][r].drawOutline(g2d, offX, offY);
							
						}else{
							map[q][r].fill(g2d, offX, offY, fogColor);
						}
					}
				}
			}
		}
		
		if(hovered != null){
			hovered.draw(g2d, hoveredColor, new BasicStroke(3), offX, offY);
		}
		if(marked != null){
			marked.draw(g2d, markedColor, new BasicStroke(3), offX, offY);
		}
		if(movementRange != null && marked.getUnit() != null){
			for(int i = 0; i < movementRange.size(); i++){
				if(marked.getUnit().getPlayer() == main.getRH().getCurrentPlayer())
					movementRange.get(i).showMovement(g2d, movementRangeColor, offX, offY);
				else
					movementRange.get(i).showMovement(g2d, movementRangeEnemyColor, offX, offY);
			}
		}
		if(movementRange != null && movementRange.contains(hovered)){
			g2d.setColor(movementPathColor);
			ArrayList<Hexagon> path = Dijkstra.getMovementPath(map, this, marked, hovered);
			if(path != null){
				for (int i = 0; i < path.size() -1 ; i++){
					g2d.drawLine(path.get(i).getCenter().getX()-offX, path.get(i).getCenter().getY()-offY, 
							path.get(i+1).getCenter().getX()-offX, path.get(i+1).getCenter().getY()-offY);
				}
			}
		}
		
	}
	
	public Hexagon getInArray(Cube c){
		int q = c.getV()[2] + RADIUS;
		int r = c.getV()[0] + RADIUS + Math.min(c.getV()[2],  0);
		if(q < 0 || q > 2 * RADIUS || r < 0 || r > 2 * RADIUS) return null;
		return map[q][r];
	}
	
	public int[] getAsArray(Cube c){
		int q = c.getV()[2] + RADIUS;
		int r = c.getV()[0] + RADIUS + Math.min(c.getV()[2],  0);
		if(q < 0 || q > 2 * RADIUS || r < 0 || r > 2 * RADIUS) return null;
		int [] arr = new int[2];
		arr[0] = q;
		arr[1] = r;
		return arr;
	}
	
	public void createHexagon(int radius){
		map = new Hexagon[2*radius+1][2*radius+1];
		
		MapFactory.createTypes(main, map, radius);
		marked = null;
		hovered = null;
		this.clearMovementRange();
	}
	
	public void setMarked(Cube c){
		this.clearMovementRange();
		marked = this.getInArray(c);
		if(marked != null && marked.isOccupied()){
			getMovementRange(marked);
		}
		
	}
	
	public boolean isMarked(){
		return (marked != null);
	}
	
	public void setHovered(Cube c){
		hovered = this.getInArray(c);
	}
	
	public void setMarked(MouseEvent e){
		clicked = new Point(e.getX(), e.getY());
		screen.setDebug("Clicked @"+ clicked.getX()+" | "+clicked.getY());
		Cube c = Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen);
		this.setMarked(c);
	}
	
	public void moveTo(MouseEvent e){
		clicked = new Point(e.getX(), e.getY());
		
		Cube c = Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen);
		Hexagon target = this.getInArray(c);
		
		if(target != null && movementRange != null){
			if(target.getCoords() != marked.getCoords()){
				
				//check if unit from current player
				Unit u = marked.getUnit();
				if(u.getPlayer() == main.getCurrentPlayer()){
					if(target.getType().isMoveable()){
						//calculate tiles
						if(movementRange.contains(target)){
							int costs = Dijkstra.getMovementCost(map, this, marked, target);
							if(costs != -1 && costs <= u.getMovementSpeed()){
								//update the visibleMap of current player
								ArrayList<Hexagon> path = Dijkstra.getMovementPath(map, this, marked, target);
								updateVisibleMap(path, main.getCurrentPlayer(), u);
								
								Hexagon.moveUnitTo(u, target);
								u.moved(costs);
								marked.unitMoved();
							}
						}
						
						this.clearMovementRange();
					}
				}
			}
		}
	}
	
	public void setHovered(MouseEvent e){
		clicked = new Point(e.getX(), e.getY());
		this.setHovered(Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY()));
	}
	
	public void recreate(int newRadius){
		RADIUS = newRadius;
		createHexagon(RADIUS);
		screen.calculateOffsets();
	}
		
	public void addScreen(){
		screen = main.getGUI().getScreen();
	}
	
	public Hexagon getStartingLocation(){
		Random rand = new Random();
		int q = rand.nextInt(map.length);
		int r = rand.nextInt(map[q].length);
		while(map[q][r] == null || !map[q][r].isMoveable() || map[q][r].isOccupied()){
			q = rand.nextInt(map.length);
			r = rand.nextInt(map[q].length);
		}
		return map[q][r];
	}
	
	public ArrayList<Hexagon> getMovementRange(Hexagon start){
		ArrayList<Hexagon> results = new ArrayList<Hexagon>();
		if(start.isOccupied()){
			results = Dijkstra.getMovementRange(map, this, start);
			movementRange = results;
		}
		return results;
	}
	
	public boolean isOccupied(MouseEvent e){
		Cube c = Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen);
		Hexagon target = this.getInArray(c);
		if(target != null)
			return (target.isOccupied());
		return false;
	}
	
	public void attack(MouseEvent e){
		clicked = new Point(e.getX(), e.getY());
		screen.setDebug("Attack @"+ clicked.getX()+" | "+clicked.getY());
		Cube c = Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen);
		Hexagon target = this.getInArray(c);
		
		
		if(marked.getUnit() != null && marked.getUnit().getPlayer() == main.getRH().getCurrentPlayer()){
			if(target.isOccupied()){
				if(target.getUnit().getPlayer() != main.getRH().getCurrentPlayer()){
					marked.getUnit().attack(target.getUnit(), marked, target);
					if(marked.getUnit().getHealth() <= 0){
						marked.getUnit().handleDelete();
						marked.deleteUnit();
					}
					if(target.getUnit().getHealth() <= 0){
						target.getUnit().handleDelete();
						target.deleteUnit();
					}
				}
			}
		}
	}
	
	public void attack(Cube c){
		this.clearMovementRange();
		marked = this.getInArray(c);
		if(marked != null && marked.isOccupied()){
			getMovementRange(marked);
		}else
			this.clearMovementRange();
	}
	
	public void clearMovementRange(){
		if(movementRange != null)
			for (int i = 0; i < movementRange.size(); i++)
				movementRange.get(i).setCosts(-1);
		movementRange = null;
	}
	
	public Hexagon getMarked(){
		return marked;
	}
	
	public Unit getUnit(){
		if(isMarked())
			return marked.getUnit();
		return null;
	}
	
	public boolean isHovered(){
		return (hovered != null);
	}
	
	public Hexagon getHovered(){
		return hovered;
	}
	
	public void updateVisibleMap(ArrayList<Hexagon> path, Player cPlayer, Unit u){
		for(int i = 0; i < path.size(); i++){
			cPlayer.updateVisibleMap(this, path.get(i), u);
		}
	}
	
	public Hexagon[][] getMap(){
		return map;
	}
	
	public boolean isBuildUpon(MouseEvent e){
		Cube c = Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen);
		Hexagon target = this.getInArray(c);
		if(target != null)
			return (target.isBuildUpon());
		return false;
	}
	
	public boolean build(MouseEvent e, int nr){
		Cube c = Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen);
		Hexagon target = this.getInArray(c);
		if(target != null ){
			//check if targeted hex is in range
			if(target.getUnit() != null){
				Costs co = main.getUIH().getBuildingIcons().getCurrentIconCosts();
				if(main.getCurrentPlayer().getRessources().isHigherThan(co)){
					main.getCurrentPlayer().substractCostFromRessources(co);
					target.build(main.getCurrentPlayer(), nr);
					main.getUIH().getBuildingIcons().resetBuildingIconNr();
					return true;
				} else{
					System.out.println("Needz more money!");
				}
			}
		}
		return false;
	}
	
	public boolean produce(int nr){
		if(isMarked()){
			Costs co = main.getUIH().getBuildingMenu().getCurrentUnitIconCosts();
			if(main.getCurrentPlayer().getRessources().isHigherThan(co) && main.getCurrentPlayer().getPopCount() <= main.getCurrentPlayer().getMaxPop()){
				main.getCurrentPlayer().substractCostFromRessources(co);
				marked.getBuilding().produce(nr);
				main.getUIH().getBuildingMenu().resetUnitIconNr();
				return true;
			} else{
				System.out.println("Needz more money!");
			}
		}
		return false;
	}
	
	public boolean isBuildUpon(){
		if(marked == null) return false;
		return marked.isBuildUpon();
	}
	
	public void resetMarked(){
		marked = null;
	}
	
	public Main getMain(){
		return main;
	}
	
	public byte[] getMapAsByte(){
		byte[] result = new byte[map.length*map[0].length];
		for(int r = 0; r < map.length; r++){ //row
			for(int c = 0; c < map[r].length; c++){ //column
				if(map[r][c] != null)
					result[(r * map.length + c)] = map[r][c].getAsByte();;
			}
		}
		return result;
	}
	
	public void printMap(Server server){
		for(int r = 0; r < map.length; r++){
			for(int c = 0; c < map[r].length; c++){
				if(map[r][c] != null){
					server.append(map[r][c].getAsByte() + " ");
				}
				else
					server.append("  ");
			}
			server.log("");
		}
	}
	
	public void printMap(){
		for(int r = 0; r < map.length; r++){
			for(int c = 0; c < map[r].length; c++){
				if(map[r][c] != null)
					System.out.print(map[r][c].getAsByte() + " ");
				else
					System.out.print("  ");
			}
			System.out.println();
		}
	}
	
	public void setMapLoaded(){
		Log.d("Map loaded = true");
		mapLoaded = true;
	}
	
	public boolean mapLoaded() {
		return mapLoaded;
	}
}
