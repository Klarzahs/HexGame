package schemmer.hexagon.biomes;

public class Forest extends Biome{
	public Forest(){
		this.food = 1f;
		this.gold = 0.5f;
		this.stone = 0.5f;
		this.wood = 2f;
		
		this.name = "Forest";
		this.image = "Grass";
		this.imageAddition = "Forest";
		
		this.movementCosts = 1;
	}
}
