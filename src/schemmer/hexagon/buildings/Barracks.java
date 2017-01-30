package schemmer.hexagon.buildings;

import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.Hexagon;

public class Barracks extends Building{
	public Barracks(Main m, Hexagon h) {
		super(m, h);
		initMenu(0, 0, 0, 1);
		
		this.tTB = 1;
		try {
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_building.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load Barracks-Building image!");
		}
	}
	
	@Override
	public void buildStep(){
		super.buildStep(this);
	}
	
	public static Costs getCosts(){
		return new Costs(1, 3, 2, 0);
	}
	
	@Override
	public void unitFinished() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getImageName() {
		return "barracks";
	}
}
