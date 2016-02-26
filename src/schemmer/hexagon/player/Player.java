package schemmer.hexagon.player;

import java.util.ArrayList;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.buildings.TownCenter;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.server.Server;
import schemmer.hexagon.ui.PlayerIcon;
import schemmer.hexagon.units.Fighter;
import schemmer.hexagon.units.Hero;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.units.UnitState;
import schemmer.hexagon.units.Villager;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Log;

public class Player {
	private ArrayList<Fighter> fighters = new ArrayList<Fighter>();
	private ArrayList<Villager> villagers = new ArrayList<Villager>();
	private ArrayList<Building> buildings = new ArrayList<Building>();
	
	private boolean[][] visibleMap;		//RANGE: 0..2*radius
	private int visibleRadius = 4;
	
	private Hero hero;
	private Hexagon startingPoint;
	private PlayerIcon icon;
	
	private int woodCount = 20, foodCount = 20, stoneCount = 20, goldCount = 20, maxPop = 0;
	private int woodPR = 0, foodPR = 0, stonePR = 0, goldPR = 0;		//PR = per round
	private boolean hasRessourcesChanged = true;
	
	private PlayerColor color;
	
	public Player(boolean isAI, int i, MapHandler mh){
		color = new PlayerColor(i);
		setHero(new Hero(this));
		
		//create starting location and add hero
		startingPoint = mh.getStartingLocation();
		startingPoint.moveTo(getHero());
		if(mh.getMain() instanceof Server)
			((Server)mh.getMain()).append("SP("+i+"): "+startingPoint.getX()+" "+startingPoint.getY());
		
		//create and init the visibleMap, startingPos + ~4 Hexs
		visibleMap = new boolean[2*mh.RADIUS+1][2*mh.RADIUS+1];
		updateVisibleMap(mh, startingPoint, visibleRadius);
	}

	public Player(boolean isAI, int i, MapHandler mh, int x, int y){
		color = new PlayerColor(i);
		setHero(new Hero(this));
		
		startingPoint = mh.getInArray(Conv.pointToCube(x, y));				// TODO: check if right
		startingPoint.moveTo(getHero());
		
		Log.d("SP("+i+"): "+startingPoint.getX()+" "+startingPoint.getY());
		
		//create and init the visibleMap, startingPos + ~4 Hexs
		visibleMap = new boolean[2*mh.RADIUS+1][2*mh.RADIUS+1];
		updateVisibleMap(mh, startingPoint, visibleRadius);
	}

	public void updateVisibleMap(MapHandler mh, Hexagon hex, Unit u){
		updateVisibleMap(mh, hex, u.getMaxMovementSpeed()+1);
	}
	
	public void updateVisibleMap(MapHandler mh, Hexagon hex, int visibleRadius){
		//iterate over adjacent fields that fullfill dx+dy+dz <= radius
		for (int dx = - visibleRadius; dx <= visibleRadius; dx++){
			for(int dy = Math.max(- visibleRadius, - dx - visibleRadius); dy <= Math.min(visibleRadius,  - dx + visibleRadius); dy++){
				
				int dz = -dx - dy;
				Cube c = hex.getCoords();
				c = Cube.addCubes(c, new Cube (dx, dy, dz));
				
				int[] arr = mh.getAsArray(c);
				if(arr != null)
					visibleMap[arr[0]][arr[1]] = true;
			}
		}
	}
	
	public boolean isHexVisible(int q, int r){
		return visibleMap[q][r];
	}
	
	public void addVillager(Villager vil){
		villagers.add(vil);
	}
	
	public PlayerColor getPColor(){
		return color;
	}
	
	public void refreshAll(){
		refreshUnits();
		refreshBuildings();
		
		updateRessources();
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
	
	private void updateRessources(){
		setFoodCount(getFoodCount() + getFoodPR()); 
		setWoodCount(getWoodCount() + getWoodPR());
		setStoneCount(getStoneCount() + getStonePR());
		setGoldCount(getGoldCount() + getGoldPR());
		if(hasRessourcesChanged)
			hasRessourcesChanged = false;
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
	
	public ArrayList<Building> getBuildings(){
		return buildings;
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
		if(hasRessourcesChanged){
			foodPR = getRate(UnitState.STATE_FOOD);
			foodPR -= villagers.size();  	// cost per villager
			return foodPR;
		}
		else
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
		if(hasRessourcesChanged){
			woodPR = getRate(UnitState.STATE_WOOD);
			return woodPR;
		}
		return woodPR; 
	}

	public void setWoodPR(int woodPR) {
		this.woodPR = woodPR;
	}

	public int getStonePR() {
		if(hasRessourcesChanged){
			stonePR = getRate(UnitState.STATE_STONE);
			return stonePR;
		}
		else
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
		if(hasRessourcesChanged){
			goldPR = getRate(UnitState.STATE_GOLD);
			goldPR -= fighters.size();
			return goldPR;
		}
		else
			return goldPR;
	}

	public void setGoldPR(int goldPR) {
		this.goldPR = goldPR;
	}
	
	public boolean[][] getVisMap(){
		return visibleMap;
	}
	
	public Costs getRessources(){
		return new Costs(getFoodCount(), getWoodCount(), getStoneCount(), getGoldCount());
	}
	
	public void substractCostFromRessources(Costs co){
		Costs newRess = Costs.substract(getRessources(), co);
		int arr[] = newRess.getCosts();
		setFoodCount(arr[0]);
		setWoodCount(arr[1]);
		setStoneCount(arr[2]);
		setGoldCount(arr[3]);
	}
	
	public int getPopCount(){
		int ret = 0;
		for(int i = 0; i < villagers.size(); i++){
			ret += 1;
		}
		return ret;
	}
	
	
	private int getRate(UnitState s){
		int ret = 0;
		for (int i = 0; i < villagers.size(); i++){
			if(villagers.get(i).getState() == s)
				ret += villagers.get(i).getGatheringRate(s);
		}
		
		if(getHero().getState() == s)
			ret += getHero().getGatheringRate(s);
		return ret;
	}
	
	public void setRessourcesChanged(boolean b){
		hasRessourcesChanged = b;
	}
	
	public boolean getRessourceChanged(){
		return hasRessourcesChanged;
	}
	
	public boolean hasTownCenter(){
		for(int i = 0; i < buildings.size(); i++){
			if(buildings.get(i).getClass() == TownCenter.class)
				return true;
		}
		return false;
	}
	
	public int getTier(){
		if(hasTownCenter()) return 2;
		return 1;
	}
	
	public Hexagon getStartingPosition(){
		return startingPoint;
	}
}
