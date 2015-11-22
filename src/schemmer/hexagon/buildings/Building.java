package schemmer.hexagon.buildings;

import java.awt.image.BufferedImage;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;

public abstract class Building {
	protected BufferedImage image;
	protected int tTB; 			//time to build 
	protected int maxHealth = 100;
	protected int health = maxHealth;
	protected int createCount;
	
	protected static Costs costs;
	
	protected Hexagon field;
	
	protected Player p;
	
	public Building(Player pl, Hexagon hex){
		p = pl;
		field = hex;
		System.out.println("New building was created!");
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
		if(this.tTB > 0 && field.isOccupiedByBuilder())
			this.tTB = this.tTB - 1;
		else if(this.tTB > 0 ) System.out.println("Building needs a builder!");
	}
	
	public int gettTB(){
		return tTB;
	}
}
