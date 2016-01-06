package schemmer.hexagon.buildings;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;

public class Barracks extends Building{
	public Barracks(Player pl, Hexagon h) {
		super(pl, h);
		this.tTB = 1;
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_building.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Barracks-Building image!");
		}
	}
	
	public void buildStep(){
		super.buildStep();
		if(tTB == 0){
			try {
				String str = this.p.getPColor().getColorString();
				image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_barracks.png"));
			} catch (IOException e) {
				System.out.println("Couldn't load Barracks image!");
			}
		}
	}
	
	public static Costs getCosts(){
		return new Costs(1, 3, 2, 0);
	}
}
