package schemmer.hexagon.processes;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.game.Screen;
import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.utils.Log;

public class GameLoop extends Thread{

	final int TARGET_FPS = 60;
	final long OPTIMAL_TIME = 1000 / TARGET_FPS;   
	
	private boolean isRunning = false;
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

		// keep looping round til the game ends
		while (isRunning){
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			now = System.currentTimeMillis();
			updateLength = now - lastLoopTime;
			lastLoopTime = now;
			delta = updateLength / ((double)OPTIMAL_TIME);

			// update the frame counter
			lastFpsTime += updateLength;
			fps++;
	      
			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= 1000){
				//log("(FPS: "+fps+")");
				lastFpsTime = 0;
				fps = 0;
			}
	      
			// update the game logic
			eh.update(delta);
			mh.update(delta);
	      
			// draw everyting
			screen.repaint();
	      
			// we want each frame to take 10 milliseconds, to do this
			// we've recorded when we started the frame. We add 10 milliseconds
			// to this and then factor in the current time to give 
			// us our final value to wait for
			// remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
			try{
				Thread.sleep(lastLoopTime-System.currentTimeMillis() + OPTIMAL_TIME);
			} 
			catch(Exception e){
				log(e.getCause()+" "+e.getMessage());
			}
		}
	}
	
	public void stopThread(){
		isRunning = false;
		log("Stopped thread, exit code 0");
	}
	
	public void log(String s){
		Log.d(this.getClass().getSimpleName(), s);
	}
}
