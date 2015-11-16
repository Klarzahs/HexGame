package schemmer.hexagon.buildings;

import java.awt.image.BufferedImage;

import schemmer.hexagon.player.Player;

public abstract class Building {
	protected BufferedImage image;
	protected int tTB; 			//time to build 
	protected int maxHealth = 100;
	protected int health = maxHealth;
	protected int createCount;
	
	protected Player p;
	
	public Building(Player pl){
		p = pl;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public void createImage(){
		
	}
	
	public void create (int i){
		
	}
	
	public BufferedImage getCreateImage(int i){
		return null;
	}
	
	public void buildStep(){
		if(this.tTB > 0)
			this.tTB = this.tTB - 1;
	}
}
