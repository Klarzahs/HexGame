package schemmer.hexagon.buildings;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;

public class TownCenter extends Building{
	
	
	public TownCenter(Player p, Hexagon h){
		super(p, h);
		this.tTB = 3;
		try {
			String str = p.getPColor().getColorString();
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_building.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load TownCenter-Building image!");
		}
		
	}
	
	public void buildStep(){
		super.buildStep();
		if(tTB == 0){
			try {
				String str = this.p.getPColor().getColorString();
				image = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_towncenter.png"));
			} catch (IOException e) {
				System.out.println("Couldn't load TownCenter image!");
			}
			this.p.setMaxPop(5);
		}
	}
	
	public static Costs getCosts(){
		return new Costs(2, 5, 5, 0);
	}
}
