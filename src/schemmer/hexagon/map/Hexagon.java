package schemmer.hexagon.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import schemmer.hexagon.game.Screen;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Log;
import schemmer.hexagon.utils.Point;

public class Hexagon {
	private final static double CORNERS = 6;	// # Corners
	private static int SIZE = 50;			// in Pixel
	// width = sqrt(3)/2 * height
	
	private Point center;
	private Cube coords;
	
	public Hexagon(Cube c){
		this.coords = c;
		this.center = Conv.cubeToPixel(c);
		center = new Point(center.x + Screen.WIDTH/2, center.y + Screen.HEIGHT/2);
	}
	
	public Point hexCorner(double i){
		double angleDeg = 360/CORNERS * i + 30;
		double angleRad = Math.PI /180 * angleDeg;
		return new Point(center.x + SIZE * Math.cos(angleRad), center.y + SIZE * Math.sin(angleRad));
	}
	
	public void draw(Graphics2D g2d){
		recalculateCenter();
		g2d.setColor(new Color(255, 125, 0));
		for (int i = 0; i < CORNERS; i++){
			g2d.drawLine((int)(hexCorner(i).x), (int)(hexCorner(i).y), (int)(hexCorner((i+1)%CORNERS).x), (int)(hexCorner((i+1)%CORNERS).y));
		}
		g2d.drawString(""+(int)this.coords.getV()[0] + "|" + (int)this.coords.getV()[1]+"|"+(int)this.coords.getV()[2], (int)center.x, (int)center.y);
	}
	
	public void fill(Graphics2D g2d){
		int xs[] = new int [(int) CORNERS];
		int ys[] = new int [(int) CORNERS];
		
		for (int i = 0; i < xs.length; i++){
			xs[i] = (int) hexCorner(i).x;
			ys[i] = (int) hexCorner(i).y;
		}
		
		g2d.fillPolygon(xs, ys, (int) CORNERS);
		
	}
	
	public static double getSize(){
		return SIZE;
	}
	
	public String printCoords(){
		return "Coords: "+coords.printCube()+"\n";
	}
	
	public String printCenter(){
		return "Center @"+center.x+" | "+center.y+"\n";
	}
	
	public void draw(Graphics2D g2d, Color c, Stroke s){
		g2d.setColor(c);
		g2d.setStroke(s);
		recalculateCenter();
		for (int i = 0; i < CORNERS; i++){
			g2d.drawLine((int)(hexCorner(i).x), (int)(hexCorner(i).y), (int)(hexCorner((i+1)%CORNERS).x), (int)(hexCorner((i+1)%CORNERS).y));
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
}

