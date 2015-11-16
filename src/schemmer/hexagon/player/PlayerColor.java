package schemmer.hexagon.player;

public class PlayerColor {
	private int color;
	private String colorStr;
	
	public PlayerColor(int i){
		//TODO: Change back
		color = 0;
		switch(color){
		case 0:
			colorStr = "Black";
			break;
		case 1:
			colorStr = "Blue";
			break;
		case 2:
			colorStr = "Green";
			break;
		case 3:
			colorStr = "Purple";
			break;
		case 4:
			colorStr = "Red";
			break;
		case 5:
			colorStr = "White";
			break;
		case 6:
			colorStr = "Yellow";
			break;
		default:
			colorStr = "Black";
			break;
		}
	}
	
	public String getColorString(){
		return colorStr;
	}
	
	public int getColorInt(){
		return color;
	}
	
}
