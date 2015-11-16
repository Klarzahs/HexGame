package schemmer.hexagon.biomes;

public class GrassDesert extends Biome{
	public GrassDesert(){
		this.food = 1f;
		this.gold = 1f;
		this.stone = 1f;
		this.wood = 0.5f;
		
		this.name = "Grass Desert";
		this.image = "Sand";
		this.imageAddition = "Grassdesert";
		
		this.movementCosts = 1;
	}
}
