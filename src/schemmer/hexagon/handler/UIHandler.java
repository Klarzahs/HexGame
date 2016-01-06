package schemmer.hexagon.handler;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.ui.BuildingIconTier;
import schemmer.hexagon.ui.BuildingIconsTierOne;
import schemmer.hexagon.ui.BuildingIconsTierTwo;
import schemmer.hexagon.ui.FieldInfo;
import schemmer.hexagon.ui.PlayerIcon;
import schemmer.hexagon.ui.RessourceInfo;
import schemmer.hexagon.ui.StateIcons;
import schemmer.hexagon.ui.UnitInfo;
import schemmer.hexagon.units.Hero;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.units.Villager;

public class UIHandler {
	private ArrayList<PlayerIcon> playerIcons = new ArrayList<PlayerIcon>();
	
	private BufferedImage buttonBeigePressed;
	private BufferedImage panelBeige;
	
	private BuildingIconsTierOne buildingIconsOne;
	private BuildingIconsTierTwo buildingIconsTwo;
	private StateIcons stateIcons;
	private Building buildingMenuIcons;
	
	private FieldInfo fieldInfo;
	private UnitInfo unitInfo;
	private RessourceInfo ressourceInfo;
	
	private Main main;
	
	private int middleX, middleY;
	
	public UIHandler(Main m){
		main = m;
		for(int i = 0; i < main.getRH().getPlayerCount(); i++){
			playerIcons.add(new PlayerIcon(main.getRH().getPlayer(i)));
		}
		
		middleX = main.getGUI().getWidth()/2;
		middleY = main.getGUI().getHeight()/2;
		
		buildingIconsOne = new BuildingIconsTierOne(main, middleX, middleY);
		buildingIconsTwo = new BuildingIconsTierTwo(main, middleX, middleY);
		stateIcons = new StateIcons(main, middleX, middleY);
		
		fieldInfo = new FieldInfo(main);
		unitInfo = new UnitInfo(main);
		ressourceInfo = new RessourceInfo();
		
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
		
		// ---- Resources -----
		ressourceInfo.drawResourceInfo(g2d, middleX, middleY, currentPlayer);
		
		// ---- Players ----
		drawNextPlayers(g2d, middleX, middleY, currentPlayer, maxPlayer);
		
		// ---- Units ----
		unitInfo.drawUnitInfo(g2d, middleX, middleY);
		
		// ---- Fields ----
		fieldInfo.drawFieldInfo(g2d, middleX, middleY);
		
		// ---- Minimap ----
		drawMinimap(g2d, middleY*2, middleX/4);
		
		// ---- Unit Menu ----
		if(main.getMH().getUnit() != null)
			drawUnitMenu(g2d, middleX, middleY);
		
		// ---- Building Menu ----
		else if(main.getMH().isBuildUpon()){
			Building building = main.getMH().getMarked().getBuilding();
			getBuildingMenu().drawMenuOfBuilding(g2d, middleX, middleY, building);
		}
	}
	
	
	private void drawUnitMenu(Graphics2D g2d, int middleX, int middleY){
		// check if selected unit is a builder from current player
		if(isHeroSelected()){
			g2d.drawImage(panelBeige, middleX/4, middleY*2 - middleX/6, 440 + 3 * 70, middleX/6, null);
			buildingIconsOne.drawIcons(g2d);
			stateIcons.drawIcons(g2d);
		}
		if(isBuilderSelected() && !isHeroSelected()){
			int tier = main.getCurrentPlayer().getTier();
			if(tier == 1){
				g2d.drawImage(panelBeige, middleX/4, middleY*2 - middleX/6, 440 + 3 * 70, middleX/6, null);
				buildingIconsOne.drawIcons(g2d);
				stateIcons.drawIcons(g2d);
			}
			if(tier == 2){
				g2d.drawImage(panelBeige, middleX/4, middleY*2 - middleX/6, 440 + 3 * 70, middleX/6, null);
				buildingIconsTwo.drawIcons(g2d);
				stateIcons.drawIcons(g2d);
			}
		}
	}
	
	private void drawMinimap(Graphics2D g2d, int maxY, int size){
		g2d.drawImage(panelBeige, 0, maxY-size, size, size, null);
		
		MapHandler mh = main.getMH();
		Hexagon[][] map = mh.getMap();
		boolean[][] visibleMap = main.getCurrentPlayer().getVisMap();
		
		BufferedImage worldImage = new BufferedImage(main.getGUI().getWidth(), main.getGUI().getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D worldImage2D = worldImage.createGraphics();
		
		int offX = main.getGUI().getScreen().getOffX();
		int offY = main.getGUI().getScreen().getOffY();
		
		for (int q = map.length - 1 ; q >= 0 ; q--){		//draw backwards for overlapping images
			for (int r = map[q].length - 1; r >= 0; r--){
				if(map[q][r] != null && visibleMap[q][r]){
					map[q][r].fill(worldImage2D, offX, offY);
				}
			}
		}
		
		g2d.drawImage(worldImage, 10, maxY-size + 10, size - 20, size - 20, null);
	}
	
	public boolean isBuilderSelected(){
		Unit u = (main.getMH().getMarked() != null )? main.getMH().getMarked().getUnit() : null;
		if(u != null && u.getPlayer() == main.getCurrentPlayer() && (u.getClass() == Hero.class || u.getClass() == Villager.class )) return true;
		return false;
	}
	
	public boolean isHeroSelected(){
		Unit u = (main.getMH().getMarked() != null )? main.getMH().getMarked().getUnit() : null;
		if(u != null && u.getPlayer() == main.getCurrentPlayer() && u.getClass() == Hero.class) return true;
		return false;
	}
	
	public boolean isBuildingSelected(){
		return main.getMH().isBuildUpon();
	}
	
	public boolean cursorInIconArea(MouseEvent e){
		return cursorInIconArea(e.getX(), e.getY());
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
	
	public boolean cursorInIconArea(double x, double y){
		if(isBuilderSelected()) return getBuildingIcons().cursorInBuildingIconArea(x, y) | stateIcons.cursorInStateIconArea(x, y);
		else if(isBuildingSelected()) return getBuildingMenu().cursorInUnitIconArea(x, y) | stateIcons.cursorInStateIconArea(x, y);
		return false;
	}
	
	public void resetAllIcons(){
		getBuildingIcons().resetBuildingIconNr();
		if(getBuildingMenu() != null)
			getBuildingMenu().resetUnitIconNr();
		stateIcons.resetStateIconNr();
	}
	
	public BuildingIconTier getBuildingIcons(){
		int tier = main.getCurrentPlayer().getTier();
		if(tier == 1)
			return buildingIconsOne;
		//if(tier == 2)
			return buildingIconsTwo;
		
	}
	
	public StateIcons getStateIcons(){
		return stateIcons;
	}

	public void resetHoveringInformation() {
		getBuildingIcons().resetHoveringNr();
		stateIcons.resetHoveringNr();
	}
	
	public Building getBuildingMenu(){
		return main.getCurrentBuilding();
	}
	
}
