package schemmer.hexagon.utils;

public class Log {
	private static boolean DEBUG = true;
	
	public static void d(String name, String log){
		System.out.println("@"+name+": \n"+log);
	}
	
	public static void d(String log){
		if(DEBUG) 
			System.out.println(log);
	}
	
	public static void e(String name, String log){
		System.err.println("ERROR @"+name+": \n"+log);
	}
	
	public static void e(String log){
		System.err.println(log);
	}
}
