package schemmer.hexagon.game;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.handler.RoundHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.utils.AStar;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;

public class Main implements MouseListener, MouseMotionListener, KeyListener{
	private GUI gui;
	private GameLoop gl;
	private EntityHandler eh;
	private MapHandler mh;
	private RoundHandler rh;
	private final int HEIGHT = 1080;
	private final int WIDTH = 1920;
	
	// ------ UI -------
	private Cursor rightClick, normalClick, leftClick;
	BufferedImage rightClickImage, normalClickImage, leftClickImage;
	
	public static void main (String [] args) {
		Main main = new Main();
	}
	
	public Main (){
		eh = new EntityHandler();
		mh = new MapHandler(this);
		
		gui = new GUI(WIDTH, HEIGHT, true, this);
		gui.addMouseListener(this);
		gui.addMouseMotionListener(this);
		gui.addKeyListener(this);
		
		mh.addScreen();
		
		rh = new RoundHandler(mh);
		rh.createAllPlayers(1);
		
		createUI();
		
		gl = new GameLoop(this);
		gl.run();
	}
	
	
	
	private void createUI(){
		try {
			rightClickImage = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/cursorSword_bronze.png"));
			normalClickImage = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/cursorGauntlet_blue.png"));
			leftClickImage = ImageIO.read(this.getClass().getResourceAsStream("/png/etc/cursorHand_beige.png"));
			rightClick = Toolkit.getDefaultToolkit().createCustomCursor(
					rightClickImage,					
					new Point(0,0),"Right Click");
			normalClick = Toolkit.getDefaultToolkit().createCustomCursor(
					normalClickImage,					
					new Point(0,0),"Normal cursor");
			leftClick = Toolkit.getDefaultToolkit().createCustomCursor(
					leftClickImage,					
					new Point(0,0),"Left Click");
			
			this.gui.getRootPane().setCursor(normalClick);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public EntityHandler getEH(){
		return eh;
	}
	
	public MapHandler getMH(){
		return mh;
	}
	
	public GUI getGUI(){
		return gui;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//handleLeftClick(e);
		//handleRightClick(e);
		
		this.gui.getRootPane().setCursor(normalClick);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		handleLeftClick(e);
		handleRightClick(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.gui.getRootPane().setCursor(normalClick);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Cube c = Conv.pointToCube(e.getX(), e.getY());
		mh.setHovered(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		gui.getScreen().setDebug(""+e.getKeyCode());
		if(e.getModifiers() == InputEvent.CTRL_MASK){
			switch (e.getKeyCode()){
				case 521: // "+"
					Hexagon.zoomIn();
					break;
				case 45: // "-"
					Hexagon.zoomOut();
					break;
				default:
					break;
			}
			if(e.getKeyCode() <= 57 && e.getKeyCode() >=49) gui.getScreen().recreate((e.getKeyCode()-48)*2);
		}
	}
	
	//SwingUtilities.isLeftMouseButton(MouseEvent anEvent) 
	//SwingUtilities.isRightMouseButton(MouseEvent anEvent) 
	//SwingUtilities.isMiddleMouseButton(MouseEvent anEvent)
	
	private void handleLeftClick(MouseEvent e){
		if(SwingUtilities.isLeftMouseButton(e)){
			this.gui.getRootPane().setCursor(leftClick);
			mh.setMarked(e);
		}
	}
	
	private void handleRightClick(MouseEvent e){
		if(SwingUtilities.isRightMouseButton(e)){
			this.gui.getRootPane().setCursor(rightClick);
			if(mh.isMarked())
				mh.moveTo(e);
		}
	}
	
	public RoundHandler getRH(){
		return rh;
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
}
