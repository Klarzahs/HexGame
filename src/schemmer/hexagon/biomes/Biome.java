package schemmer.hexagon.biomes;


public abstract class Biome {
	protected int wood;
	protected int food;
	protected int stone;
	protected int gold;
	
	protected int movementCosts;
	
	protected String name = "";
	protected String imageAddition = "";
	protected String image = "";
	
	public String getName(){
		return name;
	}
	
	public String getImage(){
		return image;
	}
	
	public String getAddition(){
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
