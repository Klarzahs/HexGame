package schemmer.hexagon.player;

import java.util.ArrayList;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.ui.PlayerIcon;
import schemmer.hexagon.units.Fighter;
import schemmer.hexagon.units.Hero;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.units.Villager;

public class Player {
	private ArrayList<Fighter> fighters = new ArrayList<Fighter>();
	private ArrayList<Villager> villagers = new ArrayList<Villager>();
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private Hero hero;
	private Hexagon startingPoint;
	private PlayerIcon icon;
	
	private int woodCount = 0, foodCount = 0, stoneCount = 0, goldCount = 0, maxPop = 0;
	private int woodPR = 0, foodPR = 0, stonePR = 0, goldPR = 0;		//PR = per round
	
	private PlayerColor color;
	
	public Player(boolean isAI, Hexagon hex, int i){
		color = new PlayerColor(i);
		setHero(new Hero(this));
		startingPoint = hex;
		startingPoint.moveTo(getHero());
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
		if(hero != null) 	//TODO: win condition
			getHero().refresh();
	}
	
	private void refreshBuildings(){
		for(int i = 0; i < buildings.size(); i++){
			buildings.get(i).buildStep();
		}
	}
	
	public void setIcon(PlayerIcon pic){
		icon = pic;
	}
	
	public PlayerIcon getIcon(){
		return icon;
	}

	public Hero getHero() {
		return hero;
	}

	public void setHero(Hero hero) {
		this.hero = hero;
	}
	
	public void deleteUnit(Unit u){
		if(u.getClass() == Hero.class)
			this.deleteHero();
		else if (u.getClass() == Fighter.class)
			this.deleteFighter((Fighter) u);
		else if (u.getClass() == Villager.class)
			this.deleteVillager((Villager) u);
			
	}
	
	private void deleteHero(){
		hero = null;
	}
	
	private void deleteVillager(Villager v){
		villagers.remove(v);
	}
	
	private void deleteFighter(Fighter f){
		fighters.remove(f);
	}
	
	public void addBuilding(Building b){
		buildings.add(b);
	}

	public int getMaxPop() {
		return maxPop;
	}

	public void setMaxPop(int maxPop) {
		this.maxPop = maxPop;
	}

	public int getFoodCount() {
		return foodCount;
	}

	public void setFoodCount(int foodCount) {
		this.foodCount = foodCount;
	}

	public int getFoodPR() {
		return foodPR;
	}

	public void setFoodPR(int foodPR) {
		this.foodPR = foodPR;
	}

	public int getWoodCount() {
		return woodCount;
	}

	public void setWoodCount(int woodCount) {
		this.woodCount = woodCount;
	}

	public int getWoodPR() {
		return woodPR;
	}

	public void setWoodPR(int woodPR) {
		this.woodPR = woodPR;
	}

	public int getStonePR() {
		return stonePR;
	}

	public void setStonePR(int stonePR) {
		this.stonePR = stonePR;
	}

	public int getStoneCount() {
		return stoneCount;
	}

	public void setStoneCount(int stoneCount) {
		this.stoneCount = stoneCount;
	}

	public int getGoldCount() {
		return goldCount;
	}

	public void setGoldCount(int goldCount) {
		this.goldCount = goldCount;
	}

	public int getGoldPR() {
		return goldPR;
	}

	public void setGoldPR(int goldPR) {
		this.goldPR = goldPR;
	}

}
