package schemmer.hexagon.player;

import java.util.ArrayList;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.units.Fighter;
import schemmer.hexagon.units.Hero;
import schemmer.hexagon.units.Villager;
import schemmer.hexagon.utils.PlayerColor;

public class Player {
	private ArrayList<Fighter> fighters = new ArrayList<Fighter>();
	private ArrayList<Villager> villagers = new ArrayList<Villager>();
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private static Hero hero;
	private Hexagon startingPoint;
	
	private PlayerColor color;
	
	public Player(boolean isAI, Hexagon hex){
		color = new PlayerColor();
		hero = new Hero(this);
		startingPoint = hex;
		startingPoint.moveTo(hero);
	}
	
	public void createVillager(){
		Villager vil = new Villager(this);
		villagers.add(vil);
	}
	
	public PlayerColor getPColor(){
		return color;
	}
	
	public void refreshAll(){
		refreshUnits();
		refreshBuildings();
	}
	
	private void refreshUnits(){
		for (int i = 0; i < fighters.size(); i++)
			fighters.get(i).refresh();
		for (int i = 0; i < villagers.size(); i++)
			villagers.get(i).refresh();
		hero.refresh();
	}
	
	private void refreshBuildings(){
	}
}
