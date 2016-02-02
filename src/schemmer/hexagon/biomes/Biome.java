package schemmer.hexagon.biomes;


public abstract class Biome {
	protected int wood;
	protected int food;
	protected int stone;
	protected int gold;
	
	protected int movementCosts;
	
	protected String name = "";
	protected int imageAddition = 0;
	protected int image = 0;
	
	public String getName(){
		return name;
	}
	
	public int getImage(){
		return image;
	}
	
	public int getAddition(){
		return imageAddition;
	}
	
	public int getMovementCosts(){
		return movementCosts;
	}
	
	public int getWood(){
		return wood;
	}
	
	public int getFood(){
		return food;
	}
	
	public int getStone(){
		return stone;
	}
	
	public int getGold(){
		return gold;
	}
}
