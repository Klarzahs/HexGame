package schemmer.hexagon.buildings;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.units.Villager;

public class TownCenter extends Building{
	
	
	public TownCenter(Player p, Hexagon h){
		super(p, h);
		this.tTB = 1;
		
		if(TownCenter.unitIcons == null){
			try{
				TownCenter.unitIcons = new BufferedImage[1];			// villager
				unitIcons[0] = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces (Black)/pieceBlack_villager.png"));
			}catch(Exception e){
				System.out.println("Couldn't load STATIC TownCenter-Unit images!");
			}
		}
		
		producingStep = 50;			
		producableCount = 1;		
		
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_building.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load TownCenter-Building image!");
		}
		
	}
	
	public void buildStep(){
		super.buildStep();
		if(tTB == 0){
			if(!imageLoaded){
				try {
					String str = this.p.getPColor().getColorString();
					image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_towncenter.png"));
					imageLoaded = true;
					System.out.println("Updated image");
				} catch (IOException e) {
					System.out.println("Couldn't load TownCenter image!");
				}
				this.p.setMaxPop(this.p.getMaxPop() + 5);
			}else{
				if(currentlyProduced != -1){
					producingCount += producingStep;
					
					if(producingCount >= 100){
						//find space in adjacent hex's
						for(int i = 0; i < Hexagon.CORNERS; i++){
							if(this.field.neighbour(i) != null && !this.field.neighbour(i).isOccupied()){
								// create and place villager
								Villager vil = new Villager(this.p);
								this.p.addVillager(vil);
								this.field.neighbour(i).moveTo(vil);
								
								producingCount = 0;
								currentlyProduced = -1;
								break;
							}
						}
					}
				}	
			}
			
		}
	}
		
	
	public static Costs getCosts(){
		return new Costs(2, 5, 5, 0);
	}
	
	@Override
	public BufferedImage[] getUnitIcons(){
		return TownCenter.unitIcons;
	}
	
	public Object getClassOf(int i){
		switch(i){
		case 0:
			return Villager.class;
		default: 
			return Villager.class;
		}
	}
	
	public void produce(int nr){
		currentlyProduced = nr;
		System.out.println("Queued unit production");
	}
}
