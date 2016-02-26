package schemmer.hexagon.handler;

import java.util.ArrayList;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.player.PlayerColor;
import schemmer.hexagon.server.Client;
import schemmer.hexagon.server.Server;
import schemmer.hexagon.utils.Log;

public class RoundHandler {
	private ArrayList<Player> players = new ArrayList<Player>();
	private MapHandler mh;
	private int playerCount, currentPlayer, currentRound, AIcount;		// TODO: AIcount
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
	
	public void createAllPlayers(int playerCount, int AIcount){
		if(!main.isLocal){
			client.setPlayerCount();												//fetch player/AI count from server
			client.getPlayersFromServer();
		}else{
			if(playerCount < 1)
				Log.e("RoundHandler", "Max Players below 1!");
			for (int i = 0; i < playerCount; i++){
				createPC(i);
			}
			for (int i = playerCount; i < AIcount + playerCount; i++){
				createNPC(i);
			}
			this.playerCount = playerCount;
		}
	}
	
	public Player getCurrentPlayer(){
		if(players.size() == 0) return null;
		return players.get(currentPlayer);
	}
	
	public void startRound(){
		currentPlayer = 0;
		currentRound += 1;
	}
	
	public void nextPlayer(){
		if(!main.isLocal) {
			client.nextPlayer();
		}else{
			mh.resetMarked();
			getCurrentPlayer().refreshAll();
			currentPlayer += 1;
			if(currentPlayer == playerCount)
				startRound();
		}
		if(main instanceof Server)
			((Server)main).nextPlayer();
	}

	public void quicksave(){
		
	}
	
	public void quickload(){
		
	}
	
	public int getCurrentRound(){
		if(!main.isLocal) return client.getCurrentRound();
		return currentRound;
	}
	
	public int getPlayerCount(){							//if (!isLocal) => use only after setPlayerCount was invoked
		return playerCount;
	}
	
	public int getAICount(){								//if (!isLocal) => use only after setPlayerCount was invoked
		return AIcount;
	}
	
	public Player getPlayer(int i){
		return players.get(i);
	}
	
	public int getCurrentPlayerIndex(){
		return currentPlayer;
	}
	
	public void setMaxPlayers(int max){
		if(!(main instanceof Main)) Log.e("RH: shouldn't set maxPlayers while server");
		this.playerCount = max;
	}
	
	public void setMaxAIs(int max){
		if(!(main instanceof Main)) Log.e("RH: shouldn't set maxAI while server");
		this.AIcount = max;
	}
	
	public void addServerCreatedPlayer(int i, int x, int y){
		PlayerColor color = new PlayerColor(i);
		boolean isAi = false;
		if(i > playerCount) isAi = true;
		Player player = new Player(isAi, i, main.getMH(), x, y);
		players.add(player);
	}
}
