package schemmer.hexagon.handler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.game.Screen;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Dijkstra;
import schemmer.hexagon.utils.Point;

public class MapHandler {
	private Main main;
	private Screen screen;
	
	public int RADIUS = 5;
	
	private Hexagon[][] map;
	
	private Hexagon marked;
	private Hexagon hovered;
	private ArrayList<Hexagon> movementRange;
	private Point clicked;
	
	private final Color movementRangeColor = new Color(50, 50, 255, 75);
	private final Color movementPathColor = new Color(255, 50, 50, 75);
	private final Color hoveredColor = new Color(150,150,250);
	private final Color markedColor = new Color(0,0,255);
	
	public MapHandler(Main main){
		this.main = main;
		createHexagon(RADIUS);
	}
	
	public void update(double delta){
		
	}
	
	public void draw(Graphics2D g2d, int offX, int offY){
		g2d.setColor(new Color(150,150,150));
		g2d.setStroke(new BasicStroke(1));
		for (int q = map.length - 1 ; q >= 0 ; q--){		//draw backwards for overlapping images
			for (int r = map[q].length - 1; r >= 0; r--){
				if(map[q][r] != null){
					//map[q][r].fill(g2d, offX, offY);
					map[q][r].drawPicture(g2d, offX, offY);
					map[q][r].drawOutline(g2d, offX, offY);
				}
			}
		}
		if(hovered != null){
			hovered.draw(g2d, hoveredColor, new BasicStroke(3), offX, offY);
		}
		if(marked != null){
			marked.draw(g2d, markedColor, new BasicStroke(3), offX, offY);
		}
		if(movementRange != null){
			for(int i = 0; i < movementRange.size(); i++){
				movementRange.get(i).showMovement(g2d, movementRangeColor, offX, offY);
			}
		}
		if(movementRange != null && movementRange.contains(hovered)){
			ArrayList<Hexagon> path = Dijkstra.getMovementPath(map, this, marked, hovered);
			g2d.setColor(movementPathColor);
			for (int i = 0; i < path.size() -1 ; i++){
				g2d.drawLine(path.get(i).getCenter().getX()-offX, path.get(i).getCenter().getY()-offY, 
						path.get(i+1).getCenter().getX()-offX, path.get(i+1).getCenter().getY()-offY);
			}
		}
	}
	
	public Hexagon getInArray(Cube c){
		int q = c.getV()[2] + RADIUS;
		int r = c.getV()[0] + RADIUS + Math.min(c.getV()[2],  0);
		if(q < 0 || q > 2 * RADIUS || r < 0 || r > 2 * RADIUS) return null;
		return map[q][r];
	}
	
	public void createHexagon(int radius){
		map = new Hexagon[2*radius+1][2*radius+1];
		
		MapFactory.createTypes(map, radius);
		marked = null;
		hovered = null;
		this.clearMovementRange();
	}

	public void setMarked(Cube c){
		marked = this.getInArray(c);
		if(marked != null && marked.isOccupied()){
			getMovementRange(marked);
		}else
			this.clearMovementRange();
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
		this.setMarked(Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen));
	}
	
	public void moveTo(MouseEvent e){
		clicked = new Point(e.getX(), e.getY());
		screen.setDebug("MoveTo @"+ clicked.getX()+" | "+clicked.getY());
		Cube c = Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen);
		Hexagon target = this.getInArray(c);
		if(target != null && movementRange != null){
			if(target.getCoords() != marked.getCoords()){
				Unit u = marked.getUnit();
				//check if unit from other player than current
				if(u.getPlayer() == main.getCurrentPlayer()){
					if(target.getType().isMoveable()){
						//calculate tiles
						if(movementRange.contains(target)){
							int costs = Dijkstra.getMovementCost(map, this, marked, target);
							if(costs != -1 && costs <= u.getMovementSpeed()){
								Hexagon.moveUnitTo(u, target);
								u.move(costs);
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
	
	public void clearMovementRange(){
		if(movementRange != null)
			for (int i = 0; i < movementRange.size(); i++)
				movementRange.get(i).setCosts(-1);
		movementRange = null;
	}
}
