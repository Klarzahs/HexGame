package schemmer.hexagon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.PlayerColor;

public class FieldInfo {
	private BufferedImage panelBeige;
	private Main main;
	
	public FieldInfo (Main m){
		main = m;
		try {
			panelBeige = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/panel_beige.png"));
			
		} catch (IOException e) {
			System.out.println("Couldn't load an UI Image");
		}
	}
	
	public void drawFieldInfo(Graphics2D g2d, int middleX, int middleY){
		if(main.getMH().isHovered()){
			g2d.setColor(Color.BLACK);
			
			Hexagon hex = main.getMH().getHovered();
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
}
