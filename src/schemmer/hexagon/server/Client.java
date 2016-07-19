package schemmer.hexagon.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.map.HexTypeInt;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.utils.Log;

public class Client implements Runnable {	
	private Main main;
	private ClientFunctions clientFunctions;
	
	private SocketChannel channel;
	private ReadableByteChannel wrappedChannel;

	public Client(Main main){
		String serverName = "localhost";
		int port = 5555;

		this.main = main;
		clientFunctions = new ClientFunctions(this);

		try
		{
			InetSocketAddress hostAddress = new InetSocketAddress(serverName, port);
		    channel = SocketChannel.open(hostAddress);
		    channel.socket().setSoTimeout(10);
		    InputStream inStream = channel.socket().getInputStream();
		    wrappedChannel = Channels.newChannel(inStream);
			Log.d("Connected to "+channel.getLocalAddress());
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		while(true){
			try{
				this.read();
				Thread.sleep(10);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void send(String msg){
    	try{
	        byte [] message = msg.getBytes();
	        ByteBuffer buffer = ByteBuffer.wrap(message);
	        channel.write(buffer);
	        buffer.clear();
	        Log.d("Sent "+msg+" to server");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void read(){
    	try{
	    	ByteBuffer readBuffer = ByteBuffer.allocate(1000);
			readBuffer.clear();
			int length = -1;
			try{
				length = wrappedChannel.read(readBuffer);
			} catch(SocketTimeoutException e){
			} catch (IOException e){
				System.out.println("CLIENT: Reading problem, closing connection");
				close();
				System.exit(1);
				return;
			}
			if (length == -1){
				return;
			}
			readBuffer.flip();
			byte[] buff = new byte[1024];
			readBuffer.get(buff, 0, length);
			System.out.println("CLIENT: Server said: "+new String(buff));
			clientFunctions.handleMessage(new String(buff));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

	private void close(){
		try {
			//selector.close();
			wrappedChannel.close();
			channel.close();
		} catch(SocketTimeoutException e){
    	}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void nextPlayer() {
		clientFunctions.sendNextPlayer();
	}

	public int getCurrentRound() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void receivedPlayers(){
		try{
			send("clientReady");
		}catch(Exception e){
			e.printStackTrace();
		}
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
			clientFunctions.flush(s);
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
				clientFunctions.flush(s);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Main getMain(){
		return main;
	}

	
}
