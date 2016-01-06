package schemmer.hexagon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.buildings.Costs;
import schemmer.hexagon.buildings.TownCenter;
import schemmer.hexagon.game.Main;
import schemmer.hexagon.game.Screen;
import schemmer.hexagon.units.Villager;

public class BuildingMenu {
	protected int middleX, middleY;
	protected int offsetX, offsetY;
	protected int div;
	
	protected int selectedUnitNr = -1;
	
	protected Rectangle[] rects;
	protected BufferedImage panelBeige;
	protected Main main;
	
	public BuildingMenu(Main m){
		main = m; 
		middleX = Screen.WIDTH/2;
		middleY = Screen.HEIGHT/2;
		
		try{
			panelBeige = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/panel_beige.png"));
		}catch(IOException e){
			System.out.println("Couldn't load an UI Image");
		}
	}
	
	protected void initMenu(int rectCount, int offX, int offY, int tableSize){
		offsetX = offX;
		offsetY = offY;
		div = tableSize;
		
		rects = new Rectangle[rectCount];
		for(int i = 0; i < rectCount; i++){
			rects[i] = new Rectangle(middleX/4 + offsetX + (i % div) * 70, middleY*2 - middleX/8 + offsetY + (i / div) * 70, 70, 70);
		}
	}
	
	
	public void drawMenuOfBuilding(Graphics2D g2d, int middleX, int middleY, Building b){
		// check type of selected building
		if(b.getClass() == TownCenter.class){
			g2d.drawImage(panelBeige, middleX/4, middleY*2 - middleX/8, middleX/2, middleX/8, null);
			
			for (int i = 0; i < b.getProducableCount(); i++){
				
				//highlight current production
				if(i == b.getProduct()){
					g2d.setColor(Color.BLACK);
					g2d.drawRect(middleX/4 + 20, middleY*2 - middleX/8 + 10, 70, 70);
					g2d.setColor(Color.RED);
					g2d.drawRect(middleX/4 + 20, middleY*2 - middleX/8 + 10, 70, 70);
					g2d.setColor(new Color(200,50,50,150));
					if(b.isProducing()){
						if(b.getProducingCount() == 0)
							g2d.fillRect(middleX/4 + 20, middleY*2 - middleX/8 + 10, 70 , 70);
						else
							g2d.fillRect(middleX/4 + offsetX + (100 - b.getProducingCount()) + (i % div) * 70, middleY*2 - middleX/8 + offsetY + (i / div) * 70, 70 - b.getProducingCount(), 70);
					}
				}
				
				g2d.drawImage(b.getUnitIcons()[i], middleX/4 + offsetX + (i % div) * 70, middleY*2 - middleX/8 + offsetY + (i / div) * 70, null);
			}
				
		}
		
	}
	
	public void handleUnitSelection(MouseEvent e){
		int prodCount = main.getCurrentBuilding().getProducableCount();
		for(int i = 0; i < prodCount; i++){
			if(rects[i].contains(e.getX(), e.getY())){
				selectedUnitNr = i;
			}
		}
	}
	
	public boolean cursorInUnitIconArea(double x, double y){
		if(main.getMH().isBuildUpon()){
			int procCount = main.getCurrentBuilding().getProducableCount();
			if(procCount != -1){
				Rectangle icons = new Rectangle(middleX/4 + 20, middleY*2 - middleX/6 + 10, procCount * 70, 70);
				return icons.contains(x, y);
			}
		}
		return false;
	}
	
	public Costs getCurrentUnitIconCosts(){
		Object c = main.getMH().getMarked().getBuilding().getClassOf(selectedUnitNr);
		if(c == Villager.class) return Villager.getCosts();
		return null;
	}
	
	public boolean isUnitPossible(){ 
		if(selectedUnitNr < 0) return false;
		if(main.getCurrentBuilding().isProducing()) return false;
		Costs co = getCurrentUnitIconCosts();
		if(co == null) System.out.println("Unit Costs are NULL");
		if(main.getCurrentPlayer().getRessources().isHigherThan(co)) return true;
		System.out.println("Not enough ressources for unit production!");
		return false;		
	}
	
	public void resetUnitIconNr(){
		selectedUnitNr = -1;
	}
	
	public boolean isUnitIconSelected(){
		return (selectedUnitNr >= 0);
	}
	
	public int getUnitIconNr(){
		return selectedUnitNr;
	}
	
}