package schemmer.hexagon.buildings;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.ui.BuildingMenu;
import schemmer.hexagon.units.UnitState;
import schemmer.hexagon.utils.BuildingCallback;


public abstract class Building extends BuildingMenu implements BuildingCallback{

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
	
	protected Building callback; 				// gets called after buildstep is done
	
	protected static BufferedImage[] unitIcons;
	
	public Building(Main m, Hexagon hex){
		super(m);
		p = m.getCurrentPlayer();
		field = hex;
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
		System.out.println("this should NOT be called! Instead use extending class");
	}
	
	public void buildStep(Building b){
		if(this.tTB > 0 && field.isOccupiedByBuilder() && field.getUnit().getState() == UnitState.STATE_BUILDING)
			this.tTB = this.tTB - 1;
		else if(this.tTB > 0 ) System.out.println("Building needs a builder!");
		
		if(tTB == 0){
			if(!imageLoaded){
				try {
					//set callback
					callback = b;
					
					String str = this.p.getPColor().getColorString();
					image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_"+callback.getImageName()+".png"));
					imageLoaded = true;
					System.out.println("Updated image");
				} catch (IOException e) {
					System.out.println("Couldn't load "+callback.getImageName()+" image!");
				}
				this.p.setMaxPop(this.p.getMaxPop() + 5);
				Main.instance.getUIH().resetHoveringInformation();
				Main.instance.getUIH().resetAllIcons();
			}else{
				if(currentlyProduced != -1){
					producingCount += producingStep;
					
					if(producingCount >= 100){
						//callbck to execute class specific code
						callback.unitFinished();
					}
				}	
			}
			
		}
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
