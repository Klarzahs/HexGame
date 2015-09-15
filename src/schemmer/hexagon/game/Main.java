package schemmer.hexagon.game;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.processes.GameLoop;
import schemmer.hexagon.utils.Conv;
import schemmer.hexagon.utils.Cube;

public class Main implements MouseListener, MouseMotionListener, KeyListener{
	private GUI gui;
	private GameLoop gl;
	private EntityHandler eh;
	private MapHandler mh;
	
	public static void main (String [] args) {
		Main main = new Main();
	}
	
	public Main (){
		eh = new EntityHandler();
		mh = new MapHandler(this);
		gui = new GUI(1920, 1080, true, this);
		gui.addMouseListener(this);
		gui.addMouseMotionListener(this);
		gui.addKeyListener(this);
		gl = new GameLoop(this);
		
		gl.run();
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
		Cube c = Conv.pointToCube(e.getX(), e.getY());
		mh.setMarked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Cube c = Conv.pointToCube(e.getX(), e.getY());
		mh.setMarked(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
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
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
