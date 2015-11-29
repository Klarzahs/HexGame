package schemmer.hexagon.units;

import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.buildings.Farm;
import schemmer.hexagon.buildings.Lumbermill;
import schemmer.hexagon.buildings.Mine;
import schemmer.hexagon.buildings.Quarry;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.utils.Cube;

public class Villager extends Unit{
	
	
	
	public Villager(Player p){
		super(p,2);
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_villager.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Villager image!");
		}
		
		this.name = "Villager";
		this.attack = 1;
		this.defense = 0;
		
	}
	
	public int getGatheringRate(){
		ArrayList<Building> b = player.getBuildings();
		int ret;	
		
		//get base
		switch(state){
		case STATE_FOOD:
			ret = this.getField().getBiome().getFood();
		case STATE_WOOD:
			ret = this.getField().getBiome().getWood();
		case STATE_STONE:
			ret = this.getField().getBiome().getStone();
		case STATE_GOLD:
			ret = this.getField().getBiome().getGold();
		default:
			ret = this.getField().getBiome().getFood();
		}
		
		//add additional through buildings
		for(int i = 0; i < b.size(); i++){
			switch(state){
			case STATE_FOOD:
				if(b.get(i).getClass() == Farm.class && Cube.distance(this.field.getCoords(), b.get(i).getField().getCoords()) == 1) return ret + 1;
			case STATE_WOOD:
				if(b.get(i).getClass() == Lumbermill.class && Cube.distance(this.field.getCoords(), b.get(i).getField().getCoords()) == 1) return ret + 1;
			case STATE_STONE:
				if(b.get(i).getClass() == Quarry.class && Cube.distance(this.field.getCoords(), b.get(i).getField().getCoords()) == 1) return ret + 1; 
			case STATE_GOLD:
				if(b.get(i).getClass() == Mine.class && Cube.distance(this.field.getCoords(), b.get(i).getField().getCoords()) == 1) return ret + 1;
			default:
				if(b.get(i).getClass() == Farm.class && Cube.distance(this.field.getCoords(), b.get(i).getField().getCoords()) == 1) return ret + 1;
			}
		}
		return ret;
	}
	
	public static Costs getCosts(){
		return new Costs(2, 0, 0, 0);
	}
	
}
