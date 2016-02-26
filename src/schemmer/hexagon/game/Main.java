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
import java.io.DataInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import schemmer.hexagon.buildings.Building;
import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.handler.RoundHandler;
import schemmer.hexagon.handler.UIHandler;
import schemmer.hexagon.loader.Image;
import schemmer.hexagon.loader.ImageLoader;
import schemmer.hexagon.loader.ImageNumber;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.server.Client;

public class Main implements MouseListener, MouseMotionListener, KeyListener{
	private GUI gui;
	protected GameLoop gl;
	protected EntityHandler eh;
	protected MapHandler mh;
	protected RoundHandler rh;
	private ImageLoader il;
	protected final int HEIGHT = 1080;
	protected final int WIDTH = 1920;
	
	private Client client;
	public boolean isLocal;
	
	private int phase = 0;
	
	// ------ UI -------
	private Cursor rightClick, normalClick, leftClick;
	BufferedImage rightClickImage, normalClickImage, leftClickImage;
	private UIHandler uih;
	
	
	public Main (boolean isLocal, int player, int ai){
		this.isLocal = isLocal;
		if(!isLocal) {
			client = new Client();
			
			eh = new EntityHandler(client);
			mh = new MapHandler(this, client);
			mh.printMap();
			System.exit(1);
			
			
			gui = new GUI(WIDTH, HEIGHT, true, this);
			gui.addMouseListener(this);
			gui.addMouseMotionListener(this);
			gui.addKeyListener(this);
			
			mh.addScreen();
		
			rh = new RoundHandler(mh, client);
			rh.createAllPlayers(player);
			
		}else{
			
			eh = new EntityHandler();
			mh = new MapHandler(this);
			
			gui = new GUI(WIDTH, HEIGHT, true, this);
			gui.addMouseListener(this);
			gui.addMouseMotionListener(this);
			gui.addKeyListener(this);
			
			mh.addScreen();
		
			rh = new RoundHandler(mh);
			rh.createAllPlayers(player);
			rh.startRound();
		}
		
		createUI();
		
		phase = 1;
		il = new ImageLoader(this, Image.class, ImageNumber.class);
		phase = 2;
		
		gl = new GameLoop(this);
		gl.run();
	}
	
	
	
	private void createUI(){
		try {
			uih = new UIHandler(this);
			gui.getScreen().setUIH(uih);
			
			// ---- cursor ----
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
		if(uih != null){
			if(uih.isBuilderSelected() && uih.cursorInIconArea(e)) {
				uih.getBuildingIcons().handleHovering(e);
				uih.getStateIcons().handleHovering(e);
			}	
			else {
				uih.resetHoveringInformation();
			}
		}
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
				case 32: // " "
					rh.nextPlayer();
					mh.clearMovementRange();
					gui.getScreen().appendDebug(rh.getCurrentRound()+": "+ rh.getCurrentPlayer());
					break;
				case 116: // "F5" - Quicksave
					gl.pause();
					rh.quicksave();
					gl.unpause();
					break;
				case 120: // "F9" - Quickload
					gl.pause();
					rh.quickload();
					gl.unpause();
					break;
				default:
					gui.getScreen().appendDebug(""+e.getKeyCode());
					break;
			}
			if(e.getKeyCode() <= 57 && e.getKeyCode() >=49) gui.getScreen().recreate((e.getKeyCode()-48)*2);
		}
	}
	
	private void handleLeftClick(MouseEvent e){
		if(SwingUtilities.isLeftMouseButton(e)){
			this.gui.getRootPane().setCursor(leftClick);

			
			if(uih.isBuilderSelected() && uih.cursorInIconArea(e)) {
				uih.getBuildingIcons().handleBuildingSelection(e, uih.isHeroSelected());
				
				uih.getStateIcons().handleStateSelection(e);
			}
			
			else if (uih.isBuildingSelected() && uih.cursorInIconArea(e)){
				uih.getBuildingMenu().handleUnitSelection(e);
				if(uih.getBuildingMenu().isUnitIconSelected() && uih.getBuildingMenu().isUnitPossible()){
					mh.produce(uih.getBuildingMenu().getUnitIconNr());
				}
			}
			
			else {
				mh.setMarked(e);
				uih.resetAllIcons();
			}
		}
	}
	
	private void handleRightClick(MouseEvent e){
		if(SwingUtilities.isRightMouseButton(e)){
			this.gui.getRootPane().setCursor(rightClick);
			if(mh.isMarked()){
				if(mh.isOccupied(e)){
						mh.attack(e);
						uih.getBuildingIcons().resetBuildingIconNr();
				}else if(!mh.isBuildUpon(e)){
					if(uih.getBuildingIcons().isBuildingIconSelected() && uih.getBuildingIcons().isBuildingPossible()){
						mh.moveTo(e);
						mh.build(e, uih.getBuildingIcons().getBuildingIconNr());
					}else{
						mh.moveTo(e);
					}
				}
			}
		}
	}
	
	public RoundHandler getRH(){
		return rh;
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public Player getCurrentPlayer(){
		return rh.getCurrentPlayer();
	}
	
	public UIHandler getUIH(){
		return uih;
	}
	
	public Building getCurrentBuilding(){
		if(mh.getMarked() == null) 
			return null;
		return mh.getMarked().getBuilding();
	}
	
	public ImageLoader getIL(){
		return il;
	}
	
	public int getPhase(){
		return phase;
	}
	
	public Client getClient(){
		return client;
	}
	
	public Main(){} //dead constructor for Server
}
