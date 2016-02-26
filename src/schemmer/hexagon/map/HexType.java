package schemmer.hexagon.map;

import java.awt.Color;

public class HexType {
	private HexTypeInt index;
	private HexTypeColor color;
	
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
	
	public int getImage(){
		switch (index){
		case TYPE_FIELD:
			return 0;
		case TYPE_HILL:
			return -11;
		case TYPE_MOUNTAIN:
			return -11;
		default:
			return 0;
		}
	}
	
	public int getAddition(){
		switch (index){
		case TYPE_MOUNTAIN:
			return 11;
		case TYPE_DEEPWATER:
			return 0;
		case TYPE_WATER:
			return 10;
		default:
			return 0;
		}
	}
	
	public boolean isMoveable(){
		if (index == HexTypeInt.TYPE_DEEPWATER || index == HexTypeInt.TYPE_MOUNTAIN || index == HexTypeInt.TYPE_WATER)
			return false;
		return true;
	}
	
}