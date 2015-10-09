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
			return "_full";
		default:
			return "";
		}
	}
}
