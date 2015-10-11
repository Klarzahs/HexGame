package schemmer.hexagon.handler;

import java.util.ArrayList;

import schemmer.hexagon.player.Player;
import schemmer.hexagon.utils.Log;

public class RoundHandler {
	private ArrayList<Player> players = new ArrayList<Player>();
	private MapHandler mh;
	private int i;
	
	public RoundHandler(MapHandler mh){
		this.mh = mh;
	}
	
	public void createNPC(){
		Player pl = new Player(true, mh.getStartingLocation());
		players.add(pl);
	}
	
	public void createPC(){
		Player pl = new Player(true, mh.getStartingLocation());
		players.add(pl);
	}
	
	public void createAllPlayers(int max){
		if(max < 1)
			Log.e("RoundHandler", "Max Players below 1!");
		createPC();
		for (int i = 1; i < max; i++){
			createNPC();
		}
	}
	
	public Player getCurrentPlayer(){
		return players.get(i);
	}
}
