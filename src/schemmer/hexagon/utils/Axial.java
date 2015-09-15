package schemmer.hexagon.utils;

import schemmer.hexagon.game.Screen;

public class Axial {
	int[] v = new int [2];
	
	private static Axial[] directions = {
			new Axial(+1,  0), new Axial(+1, -1),new Axial( 0, -1),
			   new Axial(-1,  0), new Axial(-1, +1), new Axial( 0, +1)
	};
	
	public Axial(int q, int r){
		v[0] = q;
		v[1] = r;
	}
	
	public Axial directions(int i){
		return directions[i];
	}
	
	public Axial neighbour(int i){
		Axial d = directions(i);
		return new Axial (this.v[0] + d.v[0], this.v[1] + d.v[1]);
	}
	
	public int distance(Axial a, Axial b){
		Cube ac = Conv.axialToCube(a);
		Cube bc = Conv.axialToCube(b);
		return Cube.distance(ac, bc);
	}
	
	public static Axial round(double q, double r){
		if(q < 0 && Math.abs((q % 1)) > 0.5) q--;
		if(q > 0 && Math.abs((q % 1)) > 0.5) q++;
		if(r < 0 && Math.abs((r % 1)) > 0.5) r--;
		if(r > 0 && Math.abs((r % 1)) > 0.5) r++;
		Cube c = Conv.axialToCube(new Axial((int) q, (int) r));
		return Conv.cubeToAxial(Cube.round(c.v[0], c.v[1], c.v[2]));
	}
	
	public static Axial round(double q, double r, Screen s){
		if(q < 0 && Math.abs((q % 1)) > 0.5) q--;
		if(q > 0 && Math.abs((q % 1)) > 0.5) q++;
		if(r < 0 && Math.abs((r % 1)) > 0.5) r--;
		if(r > 0 && Math.abs((r % 1)) > 0.5) r++;
		Cube c = Conv.axialToCube(new Axial((int) q, (int) r));
		s.appenDebug("Round @"+Conv.cubeToAxial(Cube.round(c.v[0], c.v[1], c.v[2])).print());
		return Conv.cubeToAxial(Cube.round(c.v[0], c.v[1], c.v[2]));
	}
	
	public String print(){
		return v[0] + "|" + v[1];
	}
	
}
