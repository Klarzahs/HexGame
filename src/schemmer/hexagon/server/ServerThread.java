package schemmer.hexagon.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread{
	private Server server;
	private Socket client;
	private DataInputStream in;
	private DataOutputStream out;
	
	public ServerThread(Server server, Socket sck){
		try{
			this.server = server;
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
		try{
			while(in.available() > 0)
				server.log(in.readUTF());
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
	
	public void write(byte[] bytes){
		try {
			out.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
