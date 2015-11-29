package schemmer.hexagon.biomes;

public class SeasonalForest extends Biome{
	public SeasonalForest(){
		this.food = 1;
		this.gold = 1;
		this.stone = 1;
		this.wood = 1;
		
		this.name = "Seasonal Forest";
		this.image = "Grass";
		this.imageAddition = "Seasonalforest";
		
		this.movementCosts = 1;
	}
}
