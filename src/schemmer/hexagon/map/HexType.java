package schemmer.hexagon.map;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HexType {
	private HexTypeInt index;
	private HexTypeColor color;
	private String image;
	
	public HexType(int i){
		index = HexTypeInt.values()[i];
		color = HexTypeColor.values()[i];
	}
	
	public Color getColor(){
		return color.getColor();
	}
	
	public int getIndex(){
		return index.getValue();
	}
	
	public String getImage(){
		switch (index){
		case TYPE_FIELD:
			return "_tile";
		case TYPE_HILL:
			return "";
		case TYPE_MOUNTAIN:
			return "";
		default:
			return "_tile";
		}
	}
	
	public String getAddition(){
		switch (index){
		case TYPE_MOUNTAIN:
			return "rockSnow_2";
		case TYPE_DEEPWATER:
			return "addDeepwater";
		case TYPE_WATER:
			return "addWater";
		default:
			return "";
		}
	}
	
	public boolean isMoveable(){
		if (index == HexTypeInt.TYPE_DEEPWATER || index == HexTypeInt.TYPE_MOUNTAIN || index == HexTypeInt.TYPE_WATER)
			return false;
		return true;
	}
	
}