package schemmer.hexagon.units;

import java.awt.image.BufferedImage;

import schemmer.hexagon.player.Player;

public abstract class Unit {
	protected BufferedImage image;
	protected int movementSpeed;
	protected int maxMovementSpeed;
	protected Player player;
	
	public Unit(Player p, int speed){
		player = p;
		maxMovementSpeed = 3;
		movementSpeed = maxMovementSpeed;
	}
	
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
	
	public Player getPlayer(){
		return player;
	}
	
	public void refresh(){
		movementSpeed = maxMovementSpeed;
	}
}
