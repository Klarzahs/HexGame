package schemmer.hexagon.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.handler.RoundHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Log;

public class Server extends Main{
	private ServerSocket serverSocket;
	private ArrayList<ServerThread> clients = new ArrayList<ServerThread>();
	private int maxPlayerCount;
	private int maxAICount;
	private int clientReady = 0;
	
	private final static int PLAYER_COUNT = 2;
	private final static int AI_COUNT = 0;
	
	private ServerWindow window;

	public Server(int port, int player, int ai) throws IOException
	{
		createUI();
		
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
		this.maxPlayerCount = player;
		this.maxAICount = ai;

		eh = new EntityHandler();
		mh = new MapHandler(this);
		
		acceptClients();

		rh = new RoundHandler(mh);
		rh.createAllPlayers(player, ai);
		
		sendPlayerCount();
		sendPlayers();
		
		while(!clientsReady()){}		// wait
		rh.startRound();
	}

	
	private void acceptClients(){
		int nr = 0;

		log("Waiting for clients..");
		while(nr < maxPlayerCount){
			try{
				Socket client = serverSocket.accept();
				clients.add(new ServerThread(this, client, nr));
				nr++;
				log("Nr "+nr +" connected!");
			}catch(SocketTimeoutException e){
			}catch(Exception e){
				e.printStackTrace();
				log(e.getMessage());
			}
		}
	}
	
	public void append(String s){
		window.log(s);
	}
	
	public void log(String s){
		window.log(s);
		window.newLine();
	}
	
	public static void main (String [] args) {
		try{
			new Server(5555, PLAYER_COUNT, AI_COUNT);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendPlayers(){
		for(int i = 0; i < clients.size(); i++){
			clients.get(i).sendPlayers(maxPlayerCount + maxAICount);
		}
	}
	
	public void sendPlayer(Player pl, int i){
		clients.get(i).sendPlayer(pl, i);
	}
	
	public void sendPlayerCount(){
		for(int i = 0; i < clients.size(); i++){
			sendPlayerCount(i);
		}
	}
	
	public void nextPlayer(){
		//TODO: 
	}
	
	public void sendPlayerCount(int i){
		String m = "playerCount,"+maxPlayerCount+","+maxAICount;
		clients.get(i).flush(m);
	}
	
	public void clientReady(int nr){
		clientReady++;
		log("Client "+nr+" is ready: "+ clientReady);
	}
	
	private void createUI(){
		window = new ServerWindow();
		window.setSize(800, 640);
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }//windowClosing
        });
        window.setVisible(true);
	}
	
	public void attack(int nr, String m){
		String[] arr = m.split(",");
		int x = Integer.parseInt(arr[1]);
		int y = Integer.parseInt(arr[2]);
		int ex = Integer.parseInt(arr[3]);
		int ey = Integer.parseInt(arr[4]);
		Hexagon fhex = mh.getMap()[x][y];
		Hexagon ehex = mh.getMap()[ex][ey];
		Unit friend = fhex.getUnit();
		Unit enemy = ehex.getUnit();
		if(friend == null || enemy == null){
			log("State missmatch, attack canceled!");
			System.err.println("State missmatch, attack canceled!");
		}
		float dmgToEnemy = friend.getAttack() - enemy.getDefense() * ehex.getMovementCosts() / 2f;
		float dmgToYou = enemy.getAttack() - friend.getDefense() * fhex.getMovementCosts() / 2f;
		friend.setHealth(friend.getHealth() - (int)(dmgToYou * 10));
		enemy.setHealth(enemy.getHealth() - (int)(dmgToEnemy * 10));
		confirmAttack(nr, x, y, ex, ey, dmgToYou, dmgToEnemy);
	}
	
	public void confirmAttack(int nr, int x, int y, int ex, int ey, float dmgToYou, float dmgToEnemy){
		for(int i = 0; i < clients.size(); i++){
			if(i != nr)
				clients.get(i).confirmAttack(x, y, ex, ey, dmgToYou, dmgToEnemy);
		}
	}
	
	public void move(int nr, String m){
		if(clientsReady()){
			String[] arr = m.split(",");
			int fx = Integer.parseInt(arr[1]);
			int fy = Integer.parseInt(arr[2]);
			int tx = Integer.parseInt(arr[3]);
			int ty = Integer.parseInt(arr[4]);
			try{
				log("move: "+fx+" "+fy+" to "+tx+" "+ty+" ("+nr+")");
				Hexagon from = mh.getMap()[fx][fy];
				Hexagon to = mh.getMap()[tx][ty];
				Unit u = from.getUnit();
				to.moveTo(u);
				confirmMove(nr, fx, fy, tx, ty);
			}catch(Exception e){
				log(e.getMessage());
				declineMove(nr, fx, fy, tx, ty);
			}
		}
	}
	
	public void confirmMove(int nr, int fx, int fy, int tx, int ty){
		for(int i = 0; i < clients.size(); i++){
			if(i != nr)
				clients.get(i).confirmMove(fx, fy, tx, ty);
		}
	}
	
	public void declineMove(int nr, int fx, int fy, int tx, int ty){
		clients.get(nr).declineMove(fx, fy, tx, ty);
	}
	
	public void nextPlayer(int nr){
		for(int i = 0; i < clients.size(); i++){
			if(i != nr)
				clients.get(i).nextPlayer();
		}
	}
	
	private boolean clientsReady(){
		return (clientReady >= maxPlayerCount);
	}
}
