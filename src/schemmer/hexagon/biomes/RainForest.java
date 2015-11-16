package schemmer.hexagon.biomes;

public class RainForest extends Biome {
	public RainForest(){
		this.food = 1.5f;
		this.gold = 1f;
		this.stone = 0.5f;
		this.wood = 2f;
		
		this.name = "Rain Forest";
		this.image = "Grass";
		this.imageAddition = "Rainforest";
		
		this.movementCosts = 2;
	}
}
