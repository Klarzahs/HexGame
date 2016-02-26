package schemmer.hexagon.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.swing.JFrame;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.handler.RoundHandler;

public class Server extends Main{
	private ServerSocket serverSocket;
	private ArrayList<ServerThread> clients = new ArrayList<ServerThread>();
	private int maxPlayerCount;
	private boolean isLocal = true;
	private int clientReady = 0;
	
	private ServerWindow window;

	public Server(int port, int player, int ai) throws IOException
	{
		window = new ServerWindow();
		window.setSize(800, 640);
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }//windowClosing
        });
        window.setVisible(true);
		
		
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
		this.maxPlayerCount = player;

		eh = new EntityHandler();
		mh = new MapHandler(this);
		
		mh.printMap(this);
		
		acceptClients();

		sendMap();
		
		rh = new RoundHandler(mh);
		rh.createAllPlayers(player);
		while(clientReady < maxPlayerCount){}
		rh.startRound();
	}

	
	private void acceptClients(){
		int nr = 0;

		log("Waiting for clients..");
		while(nr < maxPlayerCount){
			try{
				Socket client = serverSocket.accept();
				clients.add(new ServerThread(this, client));
				nr++;
				log("Nr "+nr +" connected!");
			}catch(SocketTimeoutException e){
			}catch(Exception e){
				e.printStackTrace();
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
			new Server(5555, 3, 0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void sendMap(){
		byte message[] = mh.getMapAsByte();
		for(int i = 0; i < clients.size(); i++){
			clients.get(i).send("map");
			clients.get(i).writeInt(mh.RADIUS);
			clients.get(i).writeInt(message.length);
			clients.get(i).write(message);
		}
	}
	
	public void addReady(){
		clientReady++;
	}
}
