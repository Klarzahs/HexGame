package schemmer.hexagon.handler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.buildings.Farm;
import schemmer.hexagon.buildings.Hut;
import schemmer.hexagon.buildings.Lumbermill;
import schemmer.hexagon.buildings.TownCenter;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.ui.InfoScreen;
import schemmer.hexagon.ui.PlayerIcon;
import schemmer.hexagon.units.Hero;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.units.Villager;

public class UIHandler {
	private ArrayList<InfoScreen> UIElements = new ArrayList<InfoScreen>();
	private ArrayList<PlayerIcon> playerIcons = new ArrayList<PlayerIcon>();
	private BufferedImage buttonBeigePressed;
	private BufferedImage panelBeige;
	private Main main;
	
	private int selectedNr = -1, selectedUnitNr = -1;
	
	private int middleX, middleY;
	
	private BufferedImage[] buildingIcons = new BufferedImage[4];  		// farm, lumbermill, hut, main building
	
	public UIHandler(Main m){
		main = m;
		for(int i = 0; i < main.getRH().getPlayerCount(); i++){
			playerIcons.add(new PlayerIcon(main.getRH().getPlayer(i)));
		}
		
		middleX = main.getGUI().getWidth()/2;
		middleY = main.getGUI().getHeight()/2;
		
		try {
			buttonBeigePressed = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/buttonLong_beige_pressed.png"));
			panelBeige = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/panel_beige.png"));
			buildingIcons[0] = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces (Black)/pieceBlack_farm.png"));
			buildingIcons[1] = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces (Black)/pieceBlack_lumbermill.png"));
			buildingIcons[2] = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces (Black)/pieceBlack_hut.png"));
			buildingIcons[3] = ImageIO.read(this.getClass().getResourceAsStream("/png/pieces/Pieces (Black)/pieceBlack_towncenter.png"));
			
		} catch (IOException e) {
			System.out.println("Couldn't load an UI Image");
		}
		
	}
	
	public void draw(Graphics2D g2d){
		Player currentPlayer = main.getRH().getCurrentPlayer();
		int maxPlayer = main.getRH().getPlayerCount();
		
		// ---- Resources -----
		drawResourceInfo(g2d, middleX, middleY, currentPlayer);
		
		// ---- Players ----
		drawNextPlayers(g2d, middleX, middleY, currentPlayer, maxPlayer);
		
		// ---- Units ----
		drawUnitInfo(g2d, middleX, middleY);
		
		// ---- Fields ----
		drawFieldInfo(g2d, middleX, middleY);
		
		// ---- Minimap ----
		drawMinimap(g2d, middleY*2, middleX/4);
		
		// ---- Unit Menu ----
		if(main.getMH().getUnit() != null)
			drawUnitMenu(g2d, middleX, middleY);
		
		// ---- Building Menu ----
		else if(main.getMH().isBuildUpon()){
			Building building = main.getMH().getMarked().getBuilding();
			drawBuildingMenu(g2d, middleX, middleY, building);
		}
	}
	
	
	private void drawBuildingMenu(Graphics2D g2d, int middleX, int middleY, Building b){
		// check type of selected building
		if(b.getClass() == TownCenter.class){
			g2d.drawImage(panelBeige, middleX/4, middleY*2 - middleX/8, middleX/2, middleX/8, null);
			
			for (int i = 0; i < b.getProducableCount(); i++){
				if(i == b.getProduct()){
					g2d.setColor(Color.BLACK);
					g2d.drawRect(middleX/4 + 20, middleY*2 - middleX/8 + 10, 70, 70);
					g2d.setColor(Color.RED);
					g2d.drawRect(middleX/4 + 20, middleY*2 - middleX/8 + 10, 70, 70);
					g2d.setColor(new Color(200,50,50,150));
					if(b.isProducing())
						g2d.fillRect(middleX/4 + 20 + (100 - b.getProducingCount()), middleY*2 - middleX/8 + 10, 70 - b.getProducingCount(), 70);
				}
				
				g2d.drawImage(b.getUnitIcons()[0], middleX/4 + 20, middleY*2 - middleX/8 + 10, null);
			}
				
		}
		
	}
	
	private void drawUnitMenu(Graphics2D g2d, int middleX, int middleY){
		// check if selected unit is a builder from current player
		if(isBuilderSelected()){
			g2d.drawImage(panelBeige, middleX/4, middleY*2 - middleX/8, middleX/2, middleX/8, null);
			drawBuildingIcons(g2d, middleX, middleY);
		}
	}
	
	private void drawBuildingIcons(Graphics2D g2d, int middleX, int middleY){
		for (int i = 0; i < buildingIcons.length; i++){
			g2d.setColor(Color.BLACK);
			if(i == selectedNr) g2d.drawRect(middleX/4 + 20 + i * 70, middleY*2 - middleX/8 + 10, 70, 70);
			g2d.drawImage(buildingIcons[i], middleX/4 + 20 + i * 70, middleY*2 - middleX/8 + 10, null);
			
		}
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
		g2d.drawImage(buttonBeigePressed, middleX - 475, 40, 225, 70, null);
		
		g2d.drawString("Food: "+p.getFoodCount()+ " ("+p.getFoodPR()+")",  middleX - 465, 65);
		g2d.drawString("Wood: "+p.getWoodCount()+ " ("+p.getWoodPR()+")",  middleX - 465, 80);
		g2d.drawString("Population: "+p.getPopCount()+ "/"+p.getMaxPop(),  middleX - 465, 95);
		
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
	
	
	public boolean cursorInIconArea(double x, double y){
		if(isBuilderSelected()) return cursorInBuildingIconArea(x, y);
		else if(isBuildingSelected()) return cursorInUnitIconArea(x, y);
		return false;
	}
	
	private boolean cursorInBuildingIconArea(double x, double y){
		Rectangle icons = new Rectangle(middleX/4 + 20, middleY*2 - middleX/8 + 10, buildingIcons.length * 70, 70);
		return icons.contains(x, y);
	}
	
	private boolean cursorInUnitIconArea(double x, double y){
		if(main.getMH().isBuildUpon()){
			int temp = main.getMH().getMarked().getBuilding().getProducableCount();
			if(temp != -1){
				Rectangle icons = new Rectangle(middleX/4 + 20, middleY*2 - middleX/8 + 10, temp * 70, 70);
				return icons.contains(x, y);
			}
		}
		return false;
	}
	
	public void handleBuildingSelection(MouseEvent e, boolean heroSelected){
		Rectangle rIcon1 = new Rectangle(middleX/4 + 20, middleY*2 - middleX/8 + 10, 70, 70);
		Rectangle rIcon2 = new Rectangle(middleX/4 + 20 + 70, middleY*2 - middleX/8 + 10, 70, 70);
		Rectangle rIcon3 = new Rectangle(middleX/4 + 20 + 140, middleY*2 - middleX/8 + 10, 70, 70);
		Rectangle rIcon4 = new Rectangle(middleX/4 + 20 + 210, middleY*2 - middleX/8 + 10, 70, 70);
		if(!heroSelected){
			//TODO: Builder code
		}
		
		// farm, lumbermill, hut, main building
		if(rIcon1.contains(e.getX(), e.getY())){
			selectedNr = 0;
		}else if (rIcon2.contains(e.getX(), e.getY())){
			selectedNr = 1;
		}else if (rIcon3.contains(e.getX(), e.getY())){
			selectedNr = 2;
		}else if (rIcon4.contains(e.getX(), e.getY())){
			selectedNr = 3;
		}
	}
	
	public void handleUnitSelection(MouseEvent e){
		int prodCount = main.getMH().getMarked().getBuilding().getProducableCount();
		Rectangle[] r = new Rectangle[prodCount];
		for(int i = 0; i < prodCount; i++){
			r[i] = new Rectangle(middleX/4 + 20 + i * 70, middleY*2 - middleX/8 + 10, 70, 70);
			if(r[i].contains(e.getX(), e.getY())){
				selectedUnitNr = i;
			}
		}
	}
	
	public void resetIconNr(){
		selectedNr = -1;
	}
	
	public void resetUnitIconNr(){
		selectedUnitNr = -1;
	}
	
	public boolean isIconSelected(){
		return (selectedNr >= 0);
	}
	
	public boolean isUnitIconSelected(){
		return (selectedUnitNr >= 0);
	}
	
	public int getIconNr(){
		return selectedNr;
	}
	
	public int getUnitIconNr(){
		return selectedUnitNr;
	}
	
	public boolean isBuildingPossible(){ 
		if(selectedNr < 0) return false;
		Costs co = getCurrentIconCosts();
		if(main.getCurrentPlayer().getRessources().isHigherThan(co)) return true;
		System.out.println("Not enough ressources for building production!");
		return false;		
	}
	
	public boolean isUnitPossible(){ 
		if(selectedUnitNr < 0) return false;
		Costs co = getCurrentUnitIconCosts();
		if(co == null) System.out.println("Unit Costs are NULL");
		if(main.getCurrentPlayer().getRessources().isHigherThan(co)) return true;
		System.out.println("Not enough ressources for unit production!");
		return false;		
	}
	
	public Costs getCurrentIconCosts(){
		Costs co;
		switch(selectedNr){
		case 0:
			co = Farm.getCosts();
			break;
		case 1: 
			co = Lumbermill.getCosts();
			break;
		case 2:
			co = Hut.getCosts();
			break;
		case 3: 
			co = TownCenter.getCosts();
			break;
		default:
			co = TownCenter.getCosts();
		}
		return co;
	}
	
	public Costs getCurrentUnitIconCosts(){
		Costs co;
		Object c = main.getMH().getMarked().getBuilding().getClassOf(selectedUnitNr);
		if(c == Villager.class) return Villager.getCosts();
		return null;
	}
}
