package schemmer.hexagon.buildings;

import java.awt.image.BufferedImage;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.units.UnitState;

public abstract class Building {
	protected BufferedImage image;
	protected boolean imageLoaded = false;
	
	protected int tTB; 			//time to build 
	protected int maxHealth = 100;
	protected int health = maxHealth;
	protected int createCount;
	
	protected static Costs costs;
	
	protected Hexagon field;
	
	protected Player p;
	
	protected int producingCount = 0;			// how far is the production (of 100)?
	protected int producingStep = 0;			// how much production is gained each round?
	protected int producableCount = -1;			// how many things can be produced?
	protected int currentlyProduced = -1;		// which thing is produced (-1 == nothing)?
	
	protected static BufferedImage[] unitIcons;
	
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
		if(this.tTB > 0 && field.isOccupiedByBuilder() && field.getUnit().getState() == UnitState.STATE_BUILDING)
			this.tTB = this.tTB - 1;
		else if(this.tTB > 0 ) System.out.println("Building needs a builder!");
	}
	
	public int gettTB(){
		return tTB;
	}
	
	public boolean isProducing(){
		return (currentlyProduced >= 0);
	}
	
	public int getProducingCount(){
		return producingCount;
	}
	
	public int getProducableCount(){
		return producableCount;
	}
	
	public int getProduct(){
		return currentlyProduced;
	}
	
	public BufferedImage[] getUnitIcons(){
		return null;
	}
	
	public Object getClassOf(int i){
		return null;
	}
	
	public void produce(int nr){
	}
	
	public Hexagon getField(){
		return field;
	}
}
