package schemmer.hexagon.handler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.ui.InfoScreen;
import schemmer.hexagon.ui.PlayerIcon;
import schemmer.hexagon.units.Unit;

public class UIHandler {
	private ArrayList<InfoScreen> UIElements = new ArrayList<InfoScreen>();
	private ArrayList<PlayerIcon> playerIcons = new ArrayList<PlayerIcon>();
	private BufferedImage buttonBeigePressed;
	private BufferedImage panelBeige;
	private Main main;
	
	public UIHandler(Main m){
		main = m;
		for(int i = 0; i < main.getRH().getPlayerCount(); i++){
			playerIcons.add(new PlayerIcon(main.getRH().getPlayer(i)));
		}
		
		try {
			buttonBeigePressed = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/buttonLong_beige_pressed.png"));
			panelBeige = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/panel_beige.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load an UI Image");
		}
		
	}
	
	public void draw(Graphics2D g2d){
		Player currentPlayer = main.getRH().getCurrentPlayer();
		int maxPlayer = main.getRH().getPlayerCount();
		int middleX = main.getGUI().getWidth()/2;
		int middleY = main.getGUI().getHeight()/2;
		
		// ---- Resources -----
		drawResourceInfo(g2d, middleX, middleY, currentPlayer);
		
		// ---- Players ----
		drawNextPlayers(g2d, middleX, middleY, currentPlayer, maxPlayer);
		
		// ---- Units ----
		drawUnitInfo(g2d, middleX, middleY);
		
		// ---- Fields ----
		drawFieldInfo(g2d, middleX, middleY);
		
	}
	
	
	private void drawNextPlayers(Graphics2D g2d, int middleX, int middleY, Player currentPlayer, int maxPlayer){
		g2d.drawImage(buttonBeigePressed, middleX - 175, 40, 225, 60, null);
		g2d.drawImage(currentPlayer.getIcon().getImage(), middleX-170, 40, null);
		
		int index = main.getRH().getCurrentPlayerIndex();
		for(int i = 1; i <= 3; i++){
			Player pTemp = main.getRH().getPlayer((index + i) % maxPlayer);
			g2d.drawImage(pTemp.getIcon().getImage(), middleX - 100 + 50* (i-1), 50, 45, 45, null);
		}
		
	}
	
	private void drawResourceInfo(Graphics2D g2d, int middleX, int middleY, Player p){
		g2d.setColor(Color.BLACK);
		g2d.drawImage(buttonBeigePressed, middleX - 475, 40, 225, 60, null);
		
		g2d.drawString("Food: "+p.getFoodCount()+ " ("+p.getFoodPR()+")",  middleX - 465, 65);
		g2d.drawString("Wood: "+p.getWoodCount()+ " ("+p.getWoodPR()+")",  middleX - 465, 80);
		
		g2d.drawString("Stone: "+p.getStoneCount()+ " ("+p.getStonePR()+")",  middleX - 380, 65);
		g2d.drawString("Gold: "+p.getGoldCount()+ " ("+p.getGoldPR()+")",  middleX - 380, 80);
	}
	
	
	private void drawFieldInfo(Graphics2D g2d, int middleX, int middleY){
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
	
	private void drawUnitInfo(Graphics2D g2d, int middleX, int middleY){
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
