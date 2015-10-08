package schemmer.hexagon.handler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Point;

public class MapHandler {
	private Main main;
	private Screen screen;
	
	public int RADIUS = 15;
	
	private Hexagon[][] map;
	
	private Hexagon marked;
	private Hexagon hovered;
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
		for (int q = 0; q < map.length; q++){
			for (int r = 0; r < map[q].length; r++){
				if(map[q][r] != null){
					map[q][r].fill(g2d, offX, offY);
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
	}
	
	public void setHovered(Cube c){
		hovered = this.getInArray(c);
	}
	
	public void setMarked(MouseEvent e){
		clicked = new Point(e.getX(), e.getY());
		screen.setDebug("Clicked @"+ clicked.getX()+" | "+clicked.getY());
		this.setMarked(Conv.pointToCube(clicked.getX()+screen.getOffX(), clicked.getY()+screen.getOffY(), screen));
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
	
	
}
