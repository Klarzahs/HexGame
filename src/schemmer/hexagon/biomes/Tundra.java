package schemmer.hexagon.biomes;

public class Tundra extends Biome{
	public Tundra(){
		this.food = 2;
		this.gold = 1;
		this.stone = 1;
		this.wood = 0;
		
		this.name = "Tundra";
		this.image = 19;
		this.imageAddition = 9;
		
		this.movementCosts = 2;
	}
}
