package schemmer.hexagon.biomes;

public class RainForest extends Biome {
	public RainForest(){
		this.food = 2;
		this.gold = 0;
		this.stone = 0;
		this.wood = 3;
		
		this.name = "Rain Forest";
		this.image = 14;
		this.imageAddition = 4;
		
		this.movementCosts = 2;
	}
}
