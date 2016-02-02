package schemmer.hexagon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.loader.Image;
import schemmer.hexagon.loader.ImageLoader;
import schemmer.hexagon.loader.ImageNumber;
import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;

public class FieldInfo {
	@ImageNumber(number = 1)
	private static BufferedImage panelBeige;
	private Main main;
	
	public FieldInfo (Main m){
		main = m;
	}
	
	public void drawFieldInfo(Graphics2D g2d, int middleX, int middleY){
		if(main.getMH().isHovered()){
			g2d.setColor(Color.BLACK);
			
			Hexagon hex = main.getMH().getHovered();
			
			//TODO: check if in visible radius
			/*int[] v = Conv.cubeToAxial(hex.getCoords()).v;
			v[0] += main.getMH().RADIUS;
			v[1] += main.getMH().RADIUS;
			if(main.getRH().getCurrentPlayer().isHexVisible(v[1], v[0])){*/
			
				//check if biome
				if(hex != null && hex.getBiome() != null){
					String str = "Movement Cost: "+hex.getMovementCosts();
					
					g2d.drawImage(panelBeige, main.getGUI().getWidth() - 300, middleY + 100, 120, 100, null);
					g2d.drawString(hex.getBiome().getName(), main.getGUI().getWidth() - 290, middleY + 115);
					g2d.drawString(str, main.getGUI().getWidth() - 290, middleY + 130);
					//resources
					g2d.drawString("Wood: "+hex.getBiome().getWood(), main.getGUI().getWidth() - 290, middleY + 145);
					g2d.drawString("Food: "+hex.getBiome().getFood(), main.getGUI().getWidth() - 290, middleY + 160);
					g2d.drawString("Stone: "+hex.getBiome().getStone(), main.getGUI().getWidth() - 290, middleY + 175);
					g2d.drawString("Gold: "+hex.getBiome().getGold(), main.getGUI().getWidth() - 290, middleY + 190);
				}
				//check if (deep)water
				else if(hex != null && (hex.getType().getIndex() == HexTypeInt.TYPE_DEEPWATER.getValue()
						|| hex.getType().getIndex() == HexTypeInt.TYPE_WATER.getValue())){
					g2d.drawImage(panelBeige, main.getGUI().getWidth() - 300, middleY + 100, 120, 100, null);
					g2d.drawString("Water", main.getGUI().getWidth() - 290, middleY + 115);
					g2d.drawString("Not accessible", main.getGUI().getWidth() - 290, middleY + 130);
				}
			
		}
	}
	
	@Image
	public static void loadImages(GraphicsConfiguration gc){
		if(gc != null){
			panelBeige = ImageLoader.loadImage("/png/etc/panel_beige.png");
		}
	}
	
}
