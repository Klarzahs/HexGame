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

			outToServer = client.getOutputStream();
			out = new DataOutputStream(outToServer);

			write("Hello from "+ client.getLocalSocketAddress());
			inFromServer = client.getInputStream();
			in = new DataInputStream(inFromServer);

			thread = new ClientThread(this, client);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void write(String s){
		try {
			out.writeUTF(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getPlayersFromServer() {
		for(int i = 0; i < (main.getRH().getPlayerCount() + main.getRH().getAICount()); i++){
			getPlayerFromServer();
		}
	}

	public void getPlayerFromServer(){
		try{
			if(in.available() > 0 && in.readUTF().equals("player")){
				int i = in.readInt();
				int x = in.readInt();
				int y = in.readInt();
				Log.d("Received player("+i+"): "+x+" "+y);
				main.getRH().addServerCreatedPlayer(i, x, y);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
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

	public Hexagon[][] getMapFromServer(Main main) {
		String message = "";
		Hexagon[][] map;
		int radius = -1;
		message = thread.getMapStringFromServer();
		radius = Integer.parseInt(message.split("/")[0]);
		if(message.equals("") || radius == -1){
			System.out.println("Failed to get mapradius");
			return null;
		}
		
		String[] mapPartStrings = message.split(",");
		String[] hexagonChars;
		for(int i = 0; i < mapPartStrings.length - 1; i++){
			hexagonChars = mapPartStrings[i].split(".");
			for(int j = 0; j < hexagonChars.length - 1; j++){
				if(!hexagonChars[j].equals(" "))		// null hexagon
					map[i][j] = 
			}
		}

		//convert message to map
		map = new Hexagon[radius * 2 + 1][radius * 2 + 1];
		for (int q = -radius; q <= radius; q++) {
			int r1 = Math.max(-radius, -q - radius);
			int r2 = Math.min(radius, -q + radius);
			for (int r = r1; r <= r2; r++) {
				int x = r + radius;
				int y = q + radius + Math.min(0, r);
				map[x][y] = new Hexagon(main, new Cube(q, -q-r, r), x, y);
				setHexType(map[x][y], message[(radius * 2 + 1) * x + y]);
			}
		}
		main.receivedMap = true;
		return map;
	}

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
			out.writeUTF("attack");
			out.writeInt(field.getX());
			out.writeInt(field.getY());
			out.writeInt(fieldEnemy.getX());
			out.writeInt(fieldEnemy.getY());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void moveTo(Hexagon before, Hexagon after){
		try{
			if(before != null && after != null){
				out.writeUTF("move");
				out.writeInt(before.getX());
				out.writeInt(before.getY());
				out.writeInt(after.getX());
				out.writeInt(after.getY());
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
