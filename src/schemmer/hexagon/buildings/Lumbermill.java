package schemmer.hexagon.buildings;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;

public class Lumbermill extends Building{

	public Lumbermill(Player pl, Hexagon h) {
		super(pl, h);
		this.tTB = 1;
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_building.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Lumbermill-Building image!");
		}
	}
	
	public void buildStep(){
		super.buildStep();
		if(tTB == 0){
			try {
				String str = this.p.getPColor().getColorString();
				image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_lumbermill.png"));
			} catch (IOException e) {
				System.out.println("Couldn't load Lumbermill image!");
			}
		}
	}
	
	public static Costs getCosts(){
		return new Costs(1, 2, 2, 0);
	}
}
