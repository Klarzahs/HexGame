package schemmer.hexagon.buildings;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.units.Villager;

public class TownCenter extends Building{
	
	public TownCenter(Main m, Hexagon h){
		super(m, h);
		
		producingStep = 50;			
		producableCount = 1;	
		
		initMenu(producableCount, 20, 10, 1);
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
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_building.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load TownCenter-Building image!");
		}
		
	}
	
	public void buildStep(){
		super.buildStep(this);
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

	@Override
	public void unitFinished() {
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

	@Override
	public String getImageName() {
		return "towncenter";
	}
}
