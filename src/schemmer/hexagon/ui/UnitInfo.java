package schemmer.hexagon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.units.Unit;

public class UnitInfo{
	private BufferedImage panelBeige;
	private Main main;
	
	public UnitInfo (Main m){
		main = m;
		try {
			panelBeige = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/panel_beige.png"));
			
		} catch (IOException e) {
			System.out.println("Couldn't load an UI Image");
		}
	}

	public void drawUnitInfo(Graphics2D g2d, int middleX, int middleY){
		if(main.getMH().isMarked()){
			Unit u = main.getMH().getUnit();
			if(u != null){
				g2d.setColor(Color.BLACK);
				
				g2d.drawImage(panelBeige, main.getGUI().getWidth() - 300, middleY - 15, 120, 100, null);
				g2d.drawString(u.getName(), main.getGUI().getWidth() - 290, middleY );
				
				String str = "Health: "+u.getHealth()+"/"+u.getMaxHealth()+"\n";
				g2d.drawString(str, main.getGUI().getWidth() - 290, middleY + 15);
				
				str = "Movement: "+u.getMovementSpeed()+"/"+u.getMaxMovementSpeed()+"\n";
				g2d.drawString(str, main.getGUI().getWidth() - 290, middleY + 30);
				
				str = "Attack: "+u.getAttack();
				g2d.drawString(str, main.getGUI().getWidth() - 290, middleY + 45);
				
				str = "Defense: "+u.getDefense();
				g2d.drawString(str, main.getGUI().getWidth() - 290, middleY + 60);
			}
		}
	}
}
