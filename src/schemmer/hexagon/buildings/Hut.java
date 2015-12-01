package schemmer.hexagon.buildings;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;

public class Hut extends Building{

	public Hut(Player pl, Hexagon h) {
		super(pl, h);
		this.tTB = 2;
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_building.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Hut-Building image!");
		}
	}
	
	public void buildStep(){
		super.buildStep();
		if(tTB == 0){
			try {
				String str = this.p.getPColor().getColorString();
				image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_hut.png"));
			} catch (IOException e) {
				System.out.println("Couldn't load Hut image!");
			}
			this.p.setMaxPop(this.p.getMaxPop() + 3);
		}
	}
	
	public static Costs getCosts(){
		return new Costs(0, 2, 2, 0);
	}
}
