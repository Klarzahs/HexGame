package schemmer.hexagon.ui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import schemmer.hexagon.buildings.Costs;

public interface BuildingIconTier {
	public void drawIcons(Graphics2D g2d);
	public void handleBuildingSelection(MouseEvent e, boolean heroSelected);
	public Costs getCurrentIconCosts();
	public boolean isBuildingPossible();
	public void resetBuildingIconNr();
	public boolean isBuildingIconSelected();
	public int getBuildingIconNr();
	public boolean cursorInBuildingIconArea(double x, double y);
	public void handleHovering(MouseEvent e);
	public void resetHoveringNr();
}
