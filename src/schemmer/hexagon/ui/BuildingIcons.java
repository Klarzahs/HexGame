package schemmer.hexagon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.buildings.Farm;
import schemmer.hexagon.buildings.Hut;
import schemmer.hexagon.buildings.Lumbermill;
import schemmer.hexagon.buildings.TownCenter;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.utils.HoverableIcon;

public class BuildingIcons extends HoverableIcon{
	
	private BufferedImage[] buildingIcons = new BufferedImage[4];  		// farm, lumbermill, hut, main building
	private int selectedBuildingNr = -1;
	
	private Main main;
			
	public BuildingIcons(Main m, int middleX, int middleY){
		super(middleX, middleY, 4, 20, 10, 1);
		super.setMessages(new String[]{"Farm", "Lumbermill", "Hut", "Towncenter"});
		main = m;
		try {
			buildingIcons[0] = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_farm.png"));
			buildingIcons[1] = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_lumbermill.png"));
			buildingIcons[2] = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_hut.png"));
			buildingIcons[3] = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/iconBuilding_towncenter.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load an UI Image");
		}
	}
	
	public void drawIcons(Graphics2D g2d){
		//draw building icons
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < buildingIcons.length; i++){
			if(i == selectedBuildingNr) 
				g2d.drawRect(middleX/4 + 20 + i * 70, middleY*2 - middleX/6 + 10, 70, 70);
			g2d.drawImage(buildingIcons[i], middleX/4 + 20 + i * 70, middleY*2 - middleX/6 + 10, null);
		}
		
		//draw hovering message
		String s = getHoveringMessage();
		if(s != null){
			g2d.drawString(s, middleX/4 + 20 + selectedHoverableNr * 70, middleY*2 - middleX/6 + 10);
		}
	}
	
	public void handleBuildingSelection(MouseEvent e, boolean heroSelected){
		if(!heroSelected){
			//TODO: Builder code
		}
		for(int i = 0; i < rects.length; i++){
			if(rects[i].contains(e.getX(), e.getY()))
				selectedBuildingNr = i;
		}
	}
	
	public Costs getCurrentIconCosts(){
		Costs co;
		switch(selectedBuildingNr){
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
	
	public boolean cursorInBuildingIconArea(double x, double y){
		Rectangle icons = new Rectangle(middleX/4 + 20, middleY*2 - middleX/6 + 10, buildingIcons.length * 70, 70);
		return icons.contains(x, y);
	}
	
	public boolean isBuildingPossible(){ 
		if(getBuildingIconNr() < 0) return false;
		Costs co = getCurrentIconCosts();
		if(main.getCurrentPlayer().getRessources().isHigherThan(co)) return true;
		System.out.println("Not enough ressources for building production!");
		return false;		
	}

	public void resetBuildingIconNr(){
		selectedBuildingNr = -1;
	}
	
	public boolean isBuildingIconSelected(){
		return (selectedBuildingNr >= 0);
	}
	
	public int getBuildingIconNr(){
		return selectedBuildingNr;
	}
}
