package schemmer.hexagon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.loader.Image;
import schemmer.hexagon.loader.ImageNumber;
import schemmer.hexagon.player.Player;

public class RessourceInfo {
	@ImageNumber(number = 1)
	private static BufferedImage buttonBeigePressed;
	
	public void drawResourceInfo(Graphics2D g2d, int middleX, int middleY, Player p){
		g2d.setColor(Color.BLACK);
		g2d.drawImage(buttonBeigePressed, middleX - 475, 40, 225, 70, null);
		
		g2d.drawString("Food: "+p.getFoodCount()+ " ("+p.getFoodPR()+")",  middleX - 465, 65);
		g2d.drawString("Wood: "+p.getWoodCount()+ " ("+p.getWoodPR()+")",  middleX - 465, 80);
		g2d.drawString("Population: "+p.getPopCount()+ "/"+p.getMaxPop(),  middleX - 465, 95);
		
		g2d.drawString("Stone: "+p.getStoneCount()+ " ("+p.getStonePR()+")",  middleX - 380, 65);
		g2d.drawString("Gold: "+p.getGoldCount()+ " ("+p.getGoldPR()+")",  middleX - 380, 80);
		
	}
	
	@Image
	public static void loadImages(GraphicsConfiguration gc){
		if(gc != null){
			try {
				buttonBeigePressed = ImageIO.read(RessourceInfo.class.getResourceAsStream("/png/etc/buttonLong_beige_pressed.png"));
			} catch (IOException e) {
				System.out.println("Couldn't load an UI Image");
			}
		}
	}
}
