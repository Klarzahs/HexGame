package schemmer.hexagon.buildings;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.Hexagon;

public class Mine extends Building{

	public Mine(Main m, Hexagon h) {
		super(m, h);
		initMenu(0, 0, 0, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void unitFinished() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getImageName() {
		return "Mine";
	}

}
