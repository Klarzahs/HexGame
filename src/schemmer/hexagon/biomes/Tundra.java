package schemmer.hexagon.biomes;

public class Tundra extends Biome{
	public Tundra(){
		this.food = 2;
		this.gold = 1;
		this.stone = 1;
		this.wood = 0;
		
		this.name = "Tundra";
		this.image = "Snow";
		this.imageAddition = "Tundra";
		
		this.movementCosts = 2;
	}
}
