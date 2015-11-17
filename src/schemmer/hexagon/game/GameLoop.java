package schemmer.hexagon.game;

import java.awt.MouseInfo;
import java.awt.Point;

import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.utils.Log;

public class GameLoop extends Thread{

	final int TARGET_FPS = 60;
	final long OPTIMAL_TIME = 1000 / TARGET_FPS;   
	
	private boolean isRunning = false;
	private boolean isPaused = true;
	private long lastFpsTime = 0;
	private int fps = 0;
	
	private EntityHandler eh;
	private MapHandler mh;
	private Screen screen;
	
	public GameLoop(Main main){
		this.screen = main.getGUI().getScreen();
		this.eh = main.getEH();
		this.mh = main.getMH();
	}
	
	public void run() {
		log("Starting game loop");
		long now, updateLength;
		double delta;
		long lastLoopTime = System.currentTimeMillis();
		isRunning = true;
		isPaused = false;

		// keep looping round til the game ends
		while (isRunning){
			while(!isPaused){
				now = System.currentTimeMillis();
				updateLength = now - lastLoopTime;
				lastLoopTime = now;
				delta = updateLength / ((double)OPTIMAL_TIME);
	
				// update the frame counter
				lastFpsTime += updateLength;
				fps++;
		      
				if (lastFpsTime >= 1000){
					screen.setFPS(""+fps);
					lastFpsTime = 0;
					fps = 0;
				}
		      
				// update the game logic
				eh.update(delta);
				mh.update(delta);
		      
				// draw everyting
				screen.repaint();
		      
				moveScreen();
				
				try{
					Thread.sleep(lastLoopTime-System.currentTimeMillis() + OPTIMAL_TIME);
				} 
				catch(Exception e){
					log(e.getCause()+" "+e.getMessage());
				}
			}
		}
	}
	
	private void moveScreen(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		double x = p.getX();
		double y = p.getY();
		if (x < 200) screen.moveLeft();
		if (x > Screen.WIDTH - 200) screen.moveRight();
		if (y < 200) screen.moveUp();
		if (y > Screen.HEIGHT - 200) screen.moveDown();
	}
	
	public void stopThread(){
		isRunning = false;
		isPaused = true;
		log("Stopped thread, exit code 0");
	}
	
	public void pause(){
		isPaused = true;
		log("Paused Thread!");
	}
	
	public void unpause(){
		isPaused = false;
		log("Unpaused Thread!");
	}
	
	public void log(String s){
		Log.d(this.getClass().getSimpleName(), s);
	}
}
