package schemmer.hexagon.handler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Log;
import schemmer.hexagon.utils.Point;

public class MapHandler {
	private Main main;
	
	public int RADIUS = 7;
	
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
	
	public void draw(Graphics2D g2d){
		g2d.setColor(new Color(150,150,150));
		g2d.setStroke(new BasicStroke(1));
		for (int q = 0; q < map.length; q++){
			for (int r = 0; r < map[q].length; r++){
				if(map[q][r] != null)
					map[q][r].draw(g2d);
			}
			
		}
		if(clicked != null)
			g2d.fillRect((int)clicked.x, (int)clicked.y, 5, 5);
		if(hovered != null){
			hovered.draw(g2d, new Color(150,150,250), new BasicStroke(3));
		}
		if(marked != null){
			marked.draw(g2d, new Color(0,0,255), new BasicStroke(3));
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
		for (int q = -radius; q <= radius; q++) {
			int r1 = Math.max(-radius, -q - radius);
		    int r2 = Math.min(radius, -q + radius);
		    for (int r = r1; r <= r2; r++) {
		    	map[r + radius][q + radius + Math.min(0, r)] = new Hexagon(new Cube(q, -q-r, r));
		    }
		}
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
		main.getGUI().getScreen().setDebug("Clicked @"+ e.getX()+" | "+e.getY());
		this.setMarked(Conv.pointToCube(e.getX(), e.getY(), main.getGUI().getScreen()));
	}
	
	public void setHovered(MouseEvent e){
		clicked = new Point(e.getX(), e.getY());
		this.setHovered(Conv.pointToCube(e.getX(), e.getY()));
	}
	
	public void recreate(int newRadius){
		RADIUS = newRadius;
		createHexagon(RADIUS);
	}
		
}
