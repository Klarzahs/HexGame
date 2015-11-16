package schemmer.hexagon.biomes;

public class Tundra extends Biome{
	public Tundra(){
		this.food = 0.5f;
		this.gold = 1f;
		this.stone = 1f;
		this.wood = 0f;
		
		this.name = "Tundra";
		this.image = "Snow";
		this.imageAddition = "Tundra";
		
		this.movementCosts = 2;
	}
}
