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
	
	private PlayerColor color = new PlayerColor();
	
	public Player(boolean isAI, Hexagon hex){
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
}
