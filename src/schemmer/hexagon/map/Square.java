package schemmer.hexagon.map;

import java.awt.Graphics2D;
import java.util.Random;

public class Square {
	private int posX, posY;
	
	@Deprecated
	public Square(){
		Random r = new Random();
		posX = r.nextInt(750);
		posY = r.nextInt(590);
	}
	
	@Deprecated
	public void draw(Graphics2D g2d){
		g2d.drawRect(posX, posY, 50, 50);
	}
	
	@Deprecated
	public void move(int x, int y){
		posX = x;
		posY = y;
	}
}
