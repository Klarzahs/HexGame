package schemmer.hexagon.utils;

import java.util.Random;

public class PlayerColor {
	private int color;
	private String colorStr;
	
	public PlayerColor(){
		Random r = new Random();
		color = r.nextInt(7);
		switch(color){
		case 0:
			colorStr = "Black";
		case 1:
			colorStr = "Blue";
		case 2:
			colorStr = "Green";
		case 3:
			colorStr = "Purple";
		case 4:
			colorStr = "Red";
		case 5:
			colorStr = "White";
		case 6:
			colorStr = "Yellow";
		default:
			colorStr = "Black";
		}
	}
	
	public String getColorString(){
		return colorStr;
	}
	
}
