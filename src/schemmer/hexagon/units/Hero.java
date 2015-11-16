package schemmer.hexagon.units;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.player.Player;

public class Hero extends Unit {
	public Hero(Player p){
		super(p, 3);
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_hero.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Hero image!");
		}
		this.name = "Hero";
		this.attack = 3;
		this.defense = 2;
	}
}
