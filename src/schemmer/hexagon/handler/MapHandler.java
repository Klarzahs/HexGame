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
import schemmer.hexagon.utils.AStar;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
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
			hovered.draw(g2d, new Color(150,150,250), new BasicStroke(3), offX, offY);
		}
		if(marked != null){
			marked.draw(g2d, new Color(0,0,255), new BasicStroke(3), offX, offY);
		}
		if(movementRange != null){
			for(int i = 0; i < movementRange.size(); i++){
				movementRange.get(i).showMovement(g2d, new Color(50, 50, 200, 75), offX, offY);
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
	}

	public void setMarked(Cube c){
		marked = this.getInArray(c);
		if(marked.isOccupied()){
			getMovementRange(marked);
		}else
			movementRange = null;
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
		Hexagon h = this.getInArray(c);
		if(h != null && movementRange != null){
			if(h.getCoords() != marked.getCoords()){
				Unit u = marked.getUnit();
				if(h.getType().isMoveable()){
					//calculate tiles
					ArrayList<Hexagon> movements = AStar.calculate(this, marked, h, u.getMovementSpeed());
					
					//step through the tiles
					AStar.isDoable(u, this, marked, h, true);
					while(movements.size() > 0 && u.getMovementSpeed() >= movements.get(0).getMovementCosts()){
						Hexagon.moveUnitTo(u, h);
						marked.unitMoved();
						movements.remove(0);
					}
					movementRange = null;
					System.out.println("Rem. speed is: "+u.getMovementSpeed());
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
		int maxRange = start.getUnit().getMovementSpeed();
		for (int dx = -maxRange; dx <= maxRange; dx++){
		    for (int dy = Math.max(-maxRange, -dx-maxRange); dy <= Math.min(maxRange, -dx + maxRange); dy++){
		        int dz = -dx-dy;
		        Cube c = Cube.addCubes(start.getCoords(), new Cube(dx, dy, dz));
		        Hexagon temp = this.getInArray(c);
		        if(temp != null && temp.isMoveable() && AStar.isDoable(start.getUnit(), this, start, temp, false))
	        		results.add(temp);
		    }
		}
		movementRange = results;
		return results;
	}
	
	
}
