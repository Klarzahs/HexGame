package schemmer.hexagon.handler;

import java.util.ArrayList;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.server.Client;
import schemmer.hexagon.utils.Log;

public class RoundHandler {
	private ArrayList<Player> players = new ArrayList<Player>();
	private MapHandler mh;
	private int playerCount, currentPlayer, currentRound;
	private Main main;
	
	private Client client;
	
	public RoundHandler(MapHandler mh){
		this.mh = mh;
		main = mh.getMain();
	}
	
	public RoundHandler(MapHandler mh, Client c){
		this.mh = mh;
		client = c;
		main = mh.getMain();
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
		if(!main.isLocal){
			client.getPlayersFromServer();
		}else{
			if(max < 1)
				Log.e("RoundHandler", "Max Players below 1!");
			createPC(0);
			for (int i = 1; i < max; i++){
				createNPC(i);
			}
			playerCount = max;
		}
	}
	
	public Player getCurrentPlayer(){
		if(!main.isLocal) return client.getCurrentPlayer();
		if(players.size() == 0) return null;
		return players.get(currentPlayer);
	}
	
	public void startRound(){
		currentPlayer = 0;
		currentRound += 1;
	}
	
	public void nextPlayer(){
		if(!main.isLocal) client.nextPlayer();
		mh.resetMarked();
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
		if(!main.isLocal) return client.getCurrentRound();
		return currentRound;
	}
	
	public int getPlayerCount(){
		if(!main.isLocal) return client.getPlayerCount();
		return playerCount;
	}
	
	public Player getPlayer(int i){
		return players.get(i);
	}
	
	public int getCurrentPlayerIndex(){
		return currentPlayer;
	}
}
