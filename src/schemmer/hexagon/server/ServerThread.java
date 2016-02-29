package schemmer.hexagon.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import schemmer.hexagon.player.Player;

public class ServerThread extends Thread{
	private Server server;
	private Socket client;
	private DataInputStream in;
	private DataOutputStream out;
	
	private int nr;
	
	public ServerThread(Server server, Socket sck, int i){
		try{
			this.server = server;
			nr = i;
			client = sck;
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			
			// init client
			sendMap();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.start();
	}
	
	@Override
	public void run(){
		String message;
		try{
			while(true){
				if(in.available() > 0){
					message = in.readUTF();
					server.log("Received: "+message);
					if(message.equals("clientReady"))
						server.clientReady();
					if(message.equals("attack"))
						server.attack(nr, in.readInt(), in.readInt(), in.readInt(), in.readInt());
					if(message.equals("move"))
						server.move(nr, in.readInt(), in.readInt(), in.readInt(), in.readInt());
					if(message.equals("nextPlayer"))
						server.nextPlayer(nr);
					message = null;
				} else{
					ServerThread.sleep(50);
				}
				ServerThread.yield();
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void sendMap(){
		byte message[] = server.getMH().getMapAsByte();
		send("map");
		writeInt(server.getMH().RADIUS);
		writeInt(message.length);
		write(message);
	}
	
	public void sendPlayer(Player pl, int i){
		send("player");
		writeInt(i);
		writeInt(pl.getStartingPosition().getX());
		writeInt(pl.getStartingPosition().getY());
	}
	
	public void confirmAttack(int x, int y, int ex, int ey, float dmgToYou, float dmgToEnemy){
		send("attackConfirm");
		writeInt(x);
		writeInt(y);
		writeInt(ex);
		writeInt(ey);
		writeFloat(dmgToYou);
		writeFloat(dmgToEnemy);
	}
	
	public void confirmMove(int fx, int fy, int tx, int ty){
		server.log(nr + "move Confirm");
		send("moveConfirm");
		writeInt(fx);
		writeInt(fy);
		writeInt(tx);
		writeInt(ty);
	}
	
	public void declineMove(int fx, int fy, int tx, int ty){
		send("moveDecline");
		writeInt(fx);
		writeInt(fy);
		writeInt(tx);
		writeInt(ty);
	}
	
	public void nextPlayer(){
		send("nextPlayer");
	}
	
	public void send(String s){
		try {
			out.writeUTF(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeInt(int i){
		try {
			out.writeInt(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeFloat(float f){
		try{
			out.writeFloat(f);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void write(byte[] bytes){
		try {
			out.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
