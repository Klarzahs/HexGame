package schemmer.hexagon.units;

import java.util.ArrayList;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Farm;
import schemmer.hexagon.buildings.Lumbermill;
import schemmer.hexagon.buildings.Mine;
import schemmer.hexagon.buildings.Quarry;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.utils.Cube;

public class Builder extends Unit{
	
	public Builder(Player p, int speed) {
		super(p, speed);
	}
	
	public int getGatheringRate(UnitState s){
		ArrayList<Building> b = player.getBuildings();
		int ret;	
		
		//get base
		switch(s){
		case STATE_FOOD:
			ret = this.getField().getBiome().getFood();
			break;
		case STATE_WOOD:
			ret = this.getField().getBiome().getWood();
			break;
		case STATE_STONE:
			ret = this.getField().getBiome().getStone();
			break;
		case STATE_GOLD:
			ret = this.getField().getBiome().getGold();
			break;
		default:
			ret = this.getField().getBiome().getFood();
			break;
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
	
	public boolean isBuilder(){
		return true;
	}

}
