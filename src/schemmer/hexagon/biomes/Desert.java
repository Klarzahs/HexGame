package schemmer.hexagon.biomes;

public class Desert extends Biome{
	public Desert(){
		this.food = 0;
		this.gold = 1;
		this.stone = 1;
		this.wood = 0;
		
		this.name = "Desert";
		this.image = 18;
		this.imageAddition = 1;
		
		this.movementCosts = 2;
	}
}
