package schemmer.hexagon.game;

public class StartMenu {
	public static void main (String [] args) {
		try{
			new Main(false, 3, 0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
