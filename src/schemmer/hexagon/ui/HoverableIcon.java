package schemmer.hexagon.ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class HoverableIcon {
	protected int middleX, middleY;
	protected Rectangle[] rects;
	protected int selectedHoverableNr = -1;
	
	protected String[] messages;
	
	public HoverableIcon(int x, int y, int rectCount, int offsetX, int offsetY, int tableSize){
		middleX = x;
		middleY = y;
		
		rects = new Rectangle[rectCount];
		for(int i = 0; i < rectCount; i++){
			rects[i] = new Rectangle(middleX/4 + offsetX + (i % tableSize) * 70, middleY*2 - middleX/6 + offsetY + (i / tableSize) * 70, 70, 70);
		}
	}
	public void handleHovering(MouseEvent e){
		for(int i = 0; i < rects.length; i++){
			if(rects[i].contains(e.getX(), e.getY()))
				selectedHoverableNr = i;
		}
	}
	
	public String getHoveringMessage(){
		if(selectedHoverableNr == -1 || messages == null) return null;
		return messages[selectedHoverableNr];
	}
	
	public void resetHoveringNr(){
		selectedHoverableNr = -1;
	}
	
	public boolean isHovering(){
		return (selectedHoverableNr >= 0);
	}
	
	public int getHoveringNr(){
		return selectedHoverableNr;
	}
	
	protected void setMessages(String[] strings){
		messages = strings;
	}
	
	public abstract void drawIcons(Graphics2D g2d);
	
}
