package schemmer.hexagon.biomes;


public abstract class Biome {
	protected float wood;
	protected float food;
	protected float stone;
	protected float gold;
	
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
}
