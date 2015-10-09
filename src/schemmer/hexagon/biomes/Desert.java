package schemmer.hexagon.biomes;

public class Desert extends Biome{
	public Desert(){
		this.food = 0.5f;
		this.gold = 1f;
		this.stone = 1f;
		this.wood = 0.5f;
		
		this.name = "D";
		this.image = "tileSand";
		this.imageAddition = "";
	}
}
