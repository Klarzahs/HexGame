package schemmer.hexagon.utils;

public class Log {
	public static void d(String name, String log){
		System.out.println("@"+name+": \n"+log);
	}
	
	public static void e(String name, String log){
		System.out.println("ERROR @"+name+": \n"+log);
	}
}
