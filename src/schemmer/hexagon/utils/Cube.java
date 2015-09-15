package schemmer.hexagon.utils;

import java.util.ArrayList;

public class Cube {
	int[] v = new int [3];
	
	private static Cube[] directions = {new Cube(+1, -1,  0), new Cube(+1,  0, -1), new Cube( 0, +1, -1),
			   new Cube(-1, +1,  0), new Cube(-1,  0, +1), new Cube( 0, -1, +1)};
	
	private static Cube[] diagonals = {new Cube(+2, -1, -1), new Cube(+1, +1, -2), new Cube(-1, +2, -1), 
	                 new Cube(-2, +1, +1), new Cube(-1, -1, +2), new Cube(+1, -2, +1)};

	              
	public Cube(int x, int y, int z){
		v[0] = x;
		v[1] = y;
		v[2] = z;
	}
	
	public Cube direction(int i){
		return directions[i];
	}
	
	public Cube neighbour(int i){
		return add(this, direction(i));
	}
	
	public Cube diagonalNeighbour(int i){
        return add(this, diagonals[i]);
	}
	
	public Cube lerp(Cube a, Cube b, double t){
		double x,y,z;
		x = (a.v[0] + (b.v[0] - a.v[0]) * t);
		y = (a.v[1] + (b.v[1] - a.v[1]) * t);
		z = (a.v[2] + (b.v[2] - a.v[2]) * t);
		return new Cube((int) x, (int) y, (int) z);
	}
	
	public Cube[] getLine(Cube a, Cube b){
		int dis = distance(a,b);
		ArrayList<Cube> results = new ArrayList<Cube>();
		for (int i = 0; i < dis; i++){
			results.add(lerp(a,b, 1.0/dis * i));
		}
		return (Cube[]) results.toArray();
	}
	
	public Cube add(Cube a, Cube b){
		return new Cube(a.v[0]+b.v[0], a.v[1]+b.v[1], a.v[2]+b.v[2]);
	}
	
	public static int distance(Cube a, Cube b){
		return (Math.abs(a.v[0]-b.v[0]) + Math.abs(a.v[1]-b.v[1]) + Math.abs(a.v[2]-b.v[2])) / 2;
	}
	
	public int distance(Cube b){
		return distance(this, b);
	}
	
	public static Cube round(double x, double y, double z){
		int rx = (int) x;
		int ry = (int) y;
		int rz = (int) z;
		
		double xDiff = Math.abs(rx - x);
		double yDiff = Math.abs(ry - y);
		double zDiff = Math.abs(rz - z);

		if (xDiff > yDiff && xDiff > zDiff){
			rx = - ry - rz;
		} else if (yDiff > zDiff){
			ry = - rx - rz;
		} else{
			rz = - rx - ry;
		}

		return new Cube(rx, ry, rz);
	}
	
	public String printCube(){
		return v[0] + " | " + v[1] + " | " + v[2];
	}
	
	public int[] getV(){
		return v;
	}
	
	public void setV(int[] i){
		this.v = i;
	}
}
