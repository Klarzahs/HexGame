package schemmer.hexagon.utils;

import schemmer.hexagon.handler.Screen;
import schemmer.hexagon.map.Hexagon;

public class Conv {
	/* Helper Class to convert hex grid
	 * 
	 * Dataformat: 
	 * Axial (int[])
	 * 		[0] = q (column)
	 * 		[1] = r (row)
	 * 
	 * Cube (int[])
	 * 		[0] = x = q
	 * 		[1] = y
	 * 		[2] = z = r
	 * 
	 * Constraint: x + y + z = 0
	 */
	

	public static Axial cubeToAxial(Cube cube){
		Axial a = new Axial(cube.v[0], cube.v[2]);
		return a;
	}
	
	public static Cube axialToCube (Axial axial){
		Cube c = new Cube(axial.v[0], -axial.v[0]-axial.v[1], axial.v[1]);
		return c;
	}
	
	public static Point cubeToPixel(Cube c){
		return axialToPixel(cubeToAxial(c));
	}
	
	public static Point axialToPixel(Axial a){
		double x = Hexagon.getSize() * Math.sqrt(3) * (a.v[0] + a.v[1]/2d);
		double y = Hexagon.getSize() * 3d/2d * a.v[1];
		return new Point(x, y);
	}
	
	public static Axial pointToAxial(double x, double y){
		x = x - Screen.WIDTH/2;
		y = y - Screen.HEIGHT/2;
		double q = (x * Math.sqrt(3d)/3d - y / 3d) / Hexagon.getSize();
		double r = y * 2d/3d / Hexagon.getSize();
		return Axial.round(q, r);
	}
	
	public static Axial pointToAxial(double x, double y, Screen s){
		x = x - Screen.WIDTH/2;
		y = y - Screen.HEIGHT/2;
		double q = (x * Math.sqrt(3)/3 - y / 3) / Hexagon.getSize();
		double r = y * 2/3 / Hexagon.getSize();
		s.appenDebug("Axial @" + q + "|" +r);
		return Axial.round(q, r, s);
	}
	
	public static Cube pointToCube(double x, double y){
		return axialToCube(pointToAxial(x, y));
	}
	
	public static Cube pointToCube(double x, double y, Screen s){
		return axialToCube(pointToAxial(x, y, s));
	}
}
