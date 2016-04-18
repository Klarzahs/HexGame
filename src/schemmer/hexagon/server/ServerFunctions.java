package schemmer.hexagon.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.units.Unit;

public class ServerFunctions{
	private Server server;

	public ServerFunctions(Server server){
			this.server = server;
	}

	public void sendMap(SocketChannel c){
		//String message = server.getMH().getMapAsChar();
		String m = "map,";
		m += server.getMH().RADIUS+",";
		m += MapFactory.getMapSeed()+",";
		m += MapFactory.getBiomeSeed();
		sendMessage(c, m);
	}

	public void sendPlayers(SocketChannel c){
		server.log("Sending players..");
		for(int p = 0; p < server.getMaxCount(); p++){
			sendPlayer(c, server.getRH().getPlayer(p), p);
		}
	}

	public void sendPlayer(SocketChannel c, Player pl, int i){
		String m = "player,";
		m += i+",";
		m += pl.getStartingPosition().getX()+",";
		m += pl.getStartingPosition().getY()+"";
		this.sendMessage(c, m);
	}

	public void confirmAttack(SocketChannel c, int x, int y, int ex, int ey, float dmgToYou, float dmgToEnemy){
		String m;
		m = "attackConfirm,";
		m += x+",";
		m += y+",";
		m += ex+",";
		m += ey+",";
		m += dmgToYou+",";
		m += dmgToEnemy+"";
		sendMessage(c, m);
	}

	public void confirmMove(SocketChannel c, int fx, int fy, int tx, int ty){
		String m;
		server.log("move Confirm");
		m = "moveConfirm,";
		m += fx+",";
		m += fy+",";
		m += tx+",";
		m += ty+"";
		sendMessage(c, m);
	}

	public void declineMove(SocketChannel c, int fx, int fy, int tx, int ty){
		String m;
		m = "moveDecline,";
		m += fx+",";
		m += fy+",";
		m += tx+",";
		m += ty+"";
		sendMessage(c, m);
	}

	public void nextPlayer(SocketChannel c){
		sendMessage(c, "nextPlayer");
	}
	
	public void sendMessage(SocketChannel c, String m){
		try {
			//while(!c.isConnected()){}
			//c.write(ByteBuffer.wrap((m+"/").getBytes()));
			server.broadcast(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/*  public void write(SelectionKey key) throws IOException{
	 *  	from before: 
		 *  //byte[] hello = new String("SERVER: Hello from server").getBytes();
			//dataTracking.put(socketChannel, hello);
	 * 
	        SocketChannel channel = (SocketChannel) key.channel();
	        byte[] data = dataTracking.get(channel);
	        dataTracking.remove(channel);

	        channel.write(ByteBuffer.wrap(data));

	        key.interestOps(SelectionKey.OP_READ);
	    }*/
}
