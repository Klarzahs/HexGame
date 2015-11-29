package schemmer.hexagon.biomes;

public class Swamp extends Biome {
	public Swamp(){
		this.food = 2;
		this.gold = 0;
		this.stone = 1;
		this.wood = 2;
		
		this.name = "Swamp";
		this.image = "Magic";
		this.imageAddition = "Swamp";
		
		this.movementCosts = 2;
	}
}
