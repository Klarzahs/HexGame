package schemmer.hexagon.units;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.player.Player;

public class Villager extends Builder{
	
	public Villager(Player p){
		super(p,2);
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_villager.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Villager image!");
		}
		
		this.name = "Villager";
		this.attack = 1;
		this.defense = 0;
		
	}
	
	public static Costs getCosts(){
		return new Costs(2, 0, 0, 0);
	}
	
}
