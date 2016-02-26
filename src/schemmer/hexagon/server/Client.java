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
import schemmer.hexagon.player.Player;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.utils.Cube;

public class Client {
	private Socket client;
	private OutputStream outToServer;
	private DataOutputStream out;
	private InputStream inFromServer;
	private DataInputStream in;
	
	public Client(){
		String serverName = "localhost";
		int port = 5555;
	    try
	    {
	       client = new Socket(serverName, port);
	       
	       outToServer = client.getOutputStream();
	       out = new DataOutputStream(outToServer);
	       
	       write("Hello from "+ client.getLocalSocketAddress());
	       inFromServer = client.getInputStream();
	       in = new DataInputStream(inFromServer);
	       
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
		// TODO Auto-generated method stub
		
	}

	public Player getCurrentPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	public void nextPlayer() {
		// TODO Auto-generated method stub
		
	}

	public int getCurrentRound() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPlayerCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Hexagon[][] getMapFromServer(Main main) {
		byte[] message = new byte[0];
		Hexagon[][] map;
		int radius = -1;
		try{
			if(in.available() > 0){
				//while(true){							  // query until you get the map message
				if(in.readUTF().equals("map")){
					radius = in.readInt();
					int length = in.readInt();                    // read length of incoming message
					if(length>0) {
						message = new byte[length];
						in.readFully(message, 0, message.length); // read the message
					}
				}
				//}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(radius == -1){
			System.out.println("Failed to get mapradius");
			return null;
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
		
		return map;
	}
	
	private void setHexType(Hexagon hex, byte b){
		if(b >= 20){
			hex.setType(HexTypeInt.TYPE_HILL.getValue());
			b = (byte) (b - 20);
		}
		switch(b){
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
}
