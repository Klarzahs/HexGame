package schemmer.hexagon.biomes;

public class SeasonalForest extends Biome{
	public SeasonalForest(){
		this.food = 1f;
		this.gold = 1f;
		this.stone = 1f;
		this.wood = 0.5f;
		
		this.name = "Seasonal Forest";
		this.image = "Grass";
		this.imageAddition = "Seasonalforest";
		
		this.movementCosts = 1;
	}
}
