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
import schemmer.hexagon.units.Villager;

public class UnitIcons {
	
	private int middleX, middleY;
	private int selectedUnitNr = -1;
	private Main main;
	
	private BufferedImage panelBeige;
	
	public UnitIcons(Main m, int x, int y){
		main = m;
		middleX = x;
		middleY = y;
		try{
			panelBeige = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/panel_beige.png"));
		}catch(IOException e){
			System.out.println("Couldn't load an UI Image");
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
							g2d.fillRect(middleX/4 + 20 + (100 - b.getProducingCount()), middleY*2 - middleX/8 + 10, 70 - b.getProducingCount(), 70);
					}
				}
				
				g2d.drawImage(b.getUnitIcons()[0], middleX/4 + 20, middleY*2 - middleX/8 + 10, null);
			}
				
		}
		
	}
	
	public void handleUnitSelection(MouseEvent e){
		int prodCount = main.getMH().getMarked().getBuilding().getProducableCount();
		Rectangle[] r = new Rectangle[prodCount];
		for(int i = 0; i < prodCount; i++){
			r[i] = new Rectangle(middleX/4 + 20 + i * 70, middleY*2 - middleX/6 + 10, 70, 70);
			if(r[i].contains(e.getX(), e.getY())){
				selectedUnitNr = i;
			}
		}
	}
	
	public boolean cursorInUnitIconArea(double x, double y){
		if(main.getMH().isBuildUpon()){
			int temp = main.getMH().getMarked().getBuilding().getProducableCount();
			if(temp != -1){
				Rectangle icons = new Rectangle(middleX/4 + 20, middleY*2 - middleX/6 + 10, temp * 70, 70);
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
		if(main.getMH().getMarked().getBuilding().isProducing()) return false;
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
