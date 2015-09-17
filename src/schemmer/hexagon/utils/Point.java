package schemmer.hexagon.utils;

public class Point {
	public double x,y;
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return (int) x;
	}
	
	public int getY(){
		return (int) y;
	}
}
