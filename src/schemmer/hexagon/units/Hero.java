package schemmer.hexagon.units;

import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Farm;
import schemmer.hexagon.buildings.Lumbermill;
import schemmer.hexagon.buildings.Mine;
import schemmer.hexagon.buildings.Quarry;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.utils.Cube;

public class Hero extends Builder {
	
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
