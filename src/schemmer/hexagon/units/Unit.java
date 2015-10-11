package schemmer.hexagon.units;

import java.awt.image.BufferedImage;

public abstract class Unit {
	protected BufferedImage image;
	protected int movementSpeed;
	protected int maxMovementSpeed;
	
	public BufferedImage getImage(){
		return image;
	}
	
	public int getMovementSpeed(){
		return movementSpeed;
	}
	
	public int getMaxMovementSpeed(){
		return maxMovementSpeed;
	}
	
	public void move(int costs){
		movementSpeed -= costs;
	}
}
