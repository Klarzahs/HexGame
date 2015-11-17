package schemmer.hexagon.utils;

public class HexMath {
	public static int abs(int a){
		return (a + (a >> 31)) ^ (a >> 31);
	}
}
