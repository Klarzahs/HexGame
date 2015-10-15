package schemmer.hexagon.units;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.player.Player;

public class Villager extends Unit{
	
	public Villager(Player p){
		super(p,2);
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_border01.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Villager image!");
		}
	}
}
