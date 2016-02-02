package schemmer.hexagon.biomes;

public class GrassDesert extends Biome{
	public GrassDesert(){
		this.food = 1;
		this.gold = 1;
		this.stone = 1;
		this.wood = 0;
		
		this.name = "Grass Desert";
		this.image = 18;
		this.imageAddition = 3;
		
		this.movementCosts = 1;
	}
}
