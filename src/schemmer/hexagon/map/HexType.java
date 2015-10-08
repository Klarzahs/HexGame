package schemmer.hexagon.map;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HexType {
	private HexTypeInt index;
	private HexTypeColor color;
	private BufferedImage image;
	
	public HexType(int i){
		index = HexTypeInt.values()[i];
		color = HexTypeColor.values()[i];
	}
	
	public Color getColor(){
		return color.getColor();
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public int getIndex(){
		return index.getValue();
	}
}
