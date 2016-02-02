package schemmer.hexagon.biomes;

public class Forest extends Biome{
	public Forest(){
		this.food = 1;
		this.gold = 0;
		this.stone = 1;
		this.wood = 3;
		
		this.name = "Forest";
		this.image = 14;
		this.imageAddition = 2;
		
		this.movementCosts = 1;
	}
}
