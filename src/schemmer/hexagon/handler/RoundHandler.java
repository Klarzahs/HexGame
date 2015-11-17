package schemmer.hexagon.handler;

import java.util.ArrayList;

import schemmer.hexagon.player.Player;
import schemmer.hexagon.utils.Log;

public class RoundHandler {
	private ArrayList<Player> players = new ArrayList<Player>();
	private MapHandler mh;
	private int playerCount, currentPlayer, currentRound;
	
	public RoundHandler(MapHandler mh){
		this.mh = mh;
	}
	
	public void createNPC(int i){
		Player pl = new Player(true, i, mh);
		players.add(pl);
	}
	
	public void createPC(int i){
		Player pl = new Player(false, i, mh);
		players.add(pl);
	}
	
	public void createAllPlayers(int max){
		if(max < 1)
			Log.e("RoundHandler", "Max Players below 1!");
		createPC(0);
		for (int i = 1; i < max; i++){
			createNPC(i);
		}
		playerCount = max;
	}
	
	public Player getCurrentPlayer(){
		if(players.size() == 0) return null;
		return players.get(currentPlayer);
	}
	
	public void startRound(){
		currentPlayer = 0;
		currentRound += 1;
		for(int i = 0; i < players.size(); i++){
			players.get(i).refreshAll();
		}
	}
	
	public void nextPlayer(){
		getCurrentPlayer().refreshAll();
		currentPlayer += 1;
		if(currentPlayer == playerCount)
			startRound();
	}
	
	public void quicksave(){
		
	}
	
	public void quickload(){
		
	}
	
	public int getCurrentRound(){
		return currentRound;
	}
	
	public int getPlayerCount(){
		return playerCount;
	}
	
	public Player getPlayer(int i){
		return players.get(i);
	}
	
	public int getCurrentPlayerIndex(){
		return currentPlayer;
	}
}
