package schemmer.hexagon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.loader.Image;
import schemmer.hexagon.loader.ImageLoader;
import schemmer.hexagon.loader.ImageNumber;
import schemmer.hexagon.units.UnitState;

public class StateIcons extends HoverableIcon{
	@ImageNumber(number = 6)
	private static BufferedImage[] stateIcons = new BufferedImage[6];  		// food, wood, stone, gold, building, none
	private int selectedStateNr = -1;
	private static int div = 3;
	
	private Main main;
	
	public StateIcons(Main m, int middleX, int middleY){
		super(middleX, middleY, 6, 390, 10, div);
		super.setMessages(new String[]{"Gather Food", "Gather Wood", "Gather Stone", "Gather Gold", "Build", "Idle"});
		main = m;
	}
	
	public void drawIcons(Graphics2D g2d){
		for (int i = 0; i < stateIcons.length; i++){
			g2d.setColor(Color.BLACK);
			UnitState state = (main.getMH().getUnit() != null) ? main.getMH().getUnit().getState() : null;
			
			if(state != null && i == state.getValue()) 
				g2d.drawRect(middleX/4 + 390 + (i % div) * 70, middleY*2 - middleX/6 + 10 + (i / div) * 70, 70, 70);
			g2d.drawImage(stateIcons[i], middleX/4 + 390 + (i % div) * 70, middleY*2 - middleX/6 + 10 + (i / div) * 70, null);
			
			String s = getHoveringMessage();
			if(s != null){
				g2d.drawString(s, middleX/4 + 390 + (selectedHoverableNr % div) * 70, middleY*2 - middleX/6 + 10 + (selectedHoverableNr / div) * 70);
			}
		}
	}
	
	public void handleStateSelection(MouseEvent e){
		for(int i = 0; i < stateIcons.length; i++){
			if(rects[i].contains(e.getX(), e.getY())){
				selectedStateNr = i;
			}
		}
		
		if(selectedStateNr != -1){
			main.getMH().getUnit().setState(UnitState.getStateOfValue(selectedStateNr));
			main.getCurrentPlayer().setRessourcesChanged(true);
		}
	}
	
	public boolean cursorInStateIconArea(double x, double y){
		Rectangle icons = new Rectangle(middleX/4 + 390, middleY*2 - middleX/6 + 10 , 3 * 70, 2 * 70);
		return icons.contains(x, y);
	}
	
	
	public void resetStateIconNr(){
		selectedStateNr = -1;
	}
	
	@Image
	public static void loadImages(GraphicsConfiguration gc){
		if(gc != null){
			stateIcons[0] = ImageLoader.loadImage("/png/etc/icon_food.png");
			stateIcons[1] = ImageLoader.loadImage("/png/etc/icon_wood.png");
			stateIcons[2] = ImageLoader.loadImage("/png/etc/icon_stone.png");
			stateIcons[3] = ImageLoader.loadImage("/png/etc/icon_gold.png");
			stateIcons[4] = ImageLoader.loadImage("/png/etc/icon_build.png");
			stateIcons[5] = ImageLoader.loadImage("/png/etc/icon_none.png");
		}
	}
}
