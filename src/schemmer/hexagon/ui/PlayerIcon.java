package schemmer.hexagon.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.player.Player;

public class PlayerIcon {
	private BufferedImage icon;
	
	public PlayerIcon(Player p){
		try {
			String str = p.getPColor().getColorString();
			icon = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces ("+ str +")/piece"+ str +"_icon.png"));
			p.setIcon(this);
		} catch (IOException e) {
			System.out.println("Couldn't load PlayerIcon image for player "+p.getPColor().getColorString()+"!");
		}
	}
	
	public BufferedImage getImage(){
		return icon;
	}
}