package schemmer.hexagon.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.player.PlayerColor;

public class InfoScreen {
	protected int width, height, posX, posY;
	protected BufferedImage image;
	
	public InfoScreen(PlayerColor c){
		try {
			image = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/panel_beige.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load InfoScreen image!");
		}
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	
}
