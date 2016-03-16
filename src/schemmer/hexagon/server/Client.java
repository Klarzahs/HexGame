package schemmer.hexagon.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Cube;
import schemmer.hexagon.utils.Log;

public class Client {
	private Socket client;
	private OutputStream outToServer;
	private DataOutputStream out;
	private InputStream inFromServer;
	private DataInputStream in;
	private Main main;
	private ClientThread thread;

	public Client(Main main){
		String serverName = "localhost";
		int port = 5555;

		this.main = main;

		try
		{
			client = new Socket(serverName, port);

			client.setSendBufferSize( 256 * 1024 );
			client.setReceiveBufferSize( 256 * 1024 );
			client.setTcpNoDelay(true);
			
			thread = new ClientThread(this, client);

			outToServer = client.getOutputStream();
			out = new DataOutputStream(outToServer);

			thread.flush("Hello from "+ client.getLocalSocketAddress());
			inFromServer = client.getInputStream();
			in = new DataInputStream(inFromServer);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void getPlayerFromServer() {
		if(!thread.getPlayerFromServer())
			System.out.println("Failed to get Player!");
	}

	public void nextPlayer() {
		thread.sendNextPlayer();
	}

	public int getCurrentRound() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int setPlayerCount() {
		return thread.setPlayerCount();
	}

	public void receivedPlayers(){
		try{
			out.writeUTF("clientReady");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void getMapFromServer(Main main) {
		String message = "";
		int radius = -1;
		message = thread.getMapStringFromServer();
		
		String[] m = message.split(",");
		
		if(!m[0].equals("map")){
			return;
		}
		
		radius = Integer.parseInt(m[1]);
		if(message.equals("") || radius == -1){
			System.out.println("Failed to get mapradius");
			return;
		}
		
		MapFactory.setMapSeed(m[2]);
		MapFactory.setBiomeSeed(m[3]);
		main.getMH().createHexagon(radius);
		
		main.receivedMap = true;
	}

	@Deprecated
	private void setHexType(Hexagon hex, char c){
		hex.setType(HexTypeInt.TYPE_FIELD.getValue());
		if(c >= 20){
			hex.setType(HexTypeInt.TYPE_HILL.getValue());
			c = (char) (c - 20);
		}
		switch(c){
		case 0:
			hex.setBiome(MapFactory.desert);
			break;
		case 1:
			hex.setBiome(MapFactory.forest);
			break;
		case 2:
			hex.setBiome(MapFactory.grassDesert);
			break;
		case 3:
			hex.setBiome(MapFactory.rainForest);
			break;
		case 4:
			hex.setBiome(MapFactory.savanna);
			break;
		case 5:
			hex.setBiome(MapFactory.seasonalForest);
			break;
		case 6:
			hex.setBiome(MapFactory.swamp);
			break;
		case 7:
			hex.setBiome(MapFactory.taiga);
			break;
		case 8:
			hex.setBiome(MapFactory.tundra);
			break;
		case 9:
			hex.setType(HexTypeInt.TYPE_DEEPWATER.getValue());
			break;
		case 10:
			hex.setType(HexTypeInt.TYPE_WATER.getValue());
			break;
		case 11:
			hex.setType(HexTypeInt.TYPE_MOUNTAIN.getValue());
			break;
		}
	}

	public void attack(Hexagon field, Hexagon fieldEnemy){
		try{
			String s = "attack,";
			s += field.getX()+",";
			s += field.getY()+",";
			s += fieldEnemy.getX()+",";
			s += fieldEnemy.getY();
			thread.flush(s);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void moveTo(Hexagon before, Hexagon after){
		try{
			if(before != null && after != null){
				String s = "move,";
				s += before.getX()+",";
				s += before.getY()+",";
				s += after.getX()+",";
				s += after.getY();
				thread.flush(s);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void startListening(){
		thread.start();
	}

	public Main getMain(){
		return main;
	}

	
}
