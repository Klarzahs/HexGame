package schemmer.hexagon.units;

import java.awt.image.BufferedImage;

import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;

public abstract class Unit {
	protected BufferedImage image;
	protected int movementSpeed;
	protected int maxMovementSpeed;
	protected String name;
	protected Player player;
	
	protected int health = 100, maxHealth = 100;
	protected int attack, defense;
	
	protected Hexagon field;
	
	protected UnitState state;
	
	public Unit(Player p, int speed){
		player = p;
		maxMovementSpeed = 3;
		movementSpeed = maxMovementSpeed;
		state = UnitState.STATE_NONE;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public int getMovementSpeed(){
		return movementSpeed;
	}
	
	public int getMaxMovementSpeed(){
		return maxMovementSpeed;
	}
	
	public void moved(int costs){
		movementSpeed -= costs;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public int getHealth(){
		return health;
	}
	
	public int getMaxHealth(){
		return maxHealth;
	}
	
	public void setHealth(int h){
		health = h;
	}
	
	
	public void refresh(){
		movementSpeed = maxMovementSpeed;
	}
	
	public String getName(){
		return name;
	}
	
	public int getAttack(){
		return attack;
	}
	
	public int getDefense(){
		return defense;
	}
	
	public void attack(Unit enemy, Hexagon field, Hexagon fieldEnemy){
		float dmgToEnemy = this.attack - enemy.defense * fieldEnemy.getMovementCosts() / 2f;
		float dmgToYou = enemy.attack - this.defense * field.getMovementCosts() / 2f;
		this.setHealth(this.getHealth() - (int)(dmgToYou * 10));
		enemy.setHealth(enemy.getHealth() - (int)(dmgToEnemy * 10));
	}
	
	public boolean handleDelete(){
		//TODO: implement winning
		return true;
	}
	
	public static Costs getCosts(){
		return null;
	}
	
	public Hexagon getField(){
		return field;
	}
	
	public void setField(Hexagon h){
		field = h;
	}
	
	public void setState(UnitState s){
		state = s;
		
	}
	
	public UnitState getState(){
		return state;
	}
	
	public boolean isBuilder(){
		return false;
	}
}
