package schemmer.hexagon.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Log;

public class ClientThread extends Thread{
	private Client client;
	private PrintWriter out;
	private BufferedReader in;

	public ClientThread(Client c, Socket sock){
		this.client = c;
		try{
			out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			Log.d("ClientThread created");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		Log.d("ClientThread started");
		String message;
		try{
			while(true){
				message = in.readLine();
				if(message != null){
					Log.d("CT: received "+message);
					if(message.substring(0, "attackConfirm".length()).equals("attackConfirm"))
						confirmAttack(message);
					if(message.substring(0, "moveConfirm".length()).equals("moveConfirm"))
						confirmMove(message);
					if(message.substring(0, "moveDecline".length()).equals("moveDecline"))
						declineMove(message);
					if(message.substring(0, "nextPlayer".length()).equals("nextPlayer"))
						nextPlayer();
					this.sendAck();
				} else{
					ClientThread.sleep(50);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void reply(String message){
		try{
			out.println(message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void confirmAttack(String m){
		String[] arr = m.split(",");
		int x = Integer.parseInt(arr[1]);
		int y = Integer.parseInt(arr[2]);
		int ex = Integer.parseInt(arr[3]);
		int ey = Integer.parseInt(arr[4]);
		float dmgToYou = Float.parseFloat(arr[5]);
		float dmgToEnemy = Float.parseFloat(arr[6]);
		MapHandler mh = client.getMain().getMH();
		Hexagon fhex = mh.getMap()[x][y];
		Hexagon ehex = mh.getMap()[ex][ey];
		Unit friend = fhex.getUnit();
		Unit enemy = ehex.getUnit();
		friend.setHealth(friend.getHealth() - (int)(dmgToYou * 10));
		enemy.setHealth(enemy.getHealth() - (int)(dmgToEnemy * 10));
	}

	public void confirmMove(String m){
		String[] arr = m.split(",");
		int fx = Integer.parseInt(arr[1]);
		int fy = Integer.parseInt(arr[2]);
		int tx = Integer.parseInt(arr[3]);
		int ty = Integer.parseInt(arr[4]);
		Log.d("received confirm");
		Hexagon from = client.getMain().getMH().getMap()[fx][fy];
		Hexagon to = client.getMain().getMH().getMap()[tx][ty];
		Unit u = from.getUnit();
		Log.d("Unit is "+(u != null));
		to.moveToLocal(u);
		from.unitMoved();
	}

	//TODO: fix movement cost
	public void declineMove(String m){
		String[] arr = m.split(",");
		int fx = Integer.parseInt(arr[1]);
		int fy = Integer.parseInt(arr[2]);
		int tx = Integer.parseInt(arr[3]);
		int ty = Integer.parseInt(arr[4]);
		Hexagon from = client.getMain().getMH().getMap()[fx][fy];
		Hexagon to = client.getMain().getMH().getMap()[tx][ty];
		Unit u = to.getUnit();
		from.moveTo(u);
		to.unitMoved();
	}

	public void nextPlayer(){
		client.getMain().getRH().nextPlayerLocal();
	}

	public void sendNextPlayer(){
		out.println("nextPlayer");
	}

	private void sendAck(){
		Log.d("Sending ack..");
		reply("ack");
	}

	public int setPlayerCount() {
		try{
			String s = in.readLine();
			String[] arr = s.split(",");
			if(arr[0].equals("playerCount")){
				client.getMain().getRH().setMaxPlayers(Integer.parseInt(arr[1]));
				client.getMain().getRH().setMaxAIs(Integer.parseInt(arr[2]));
				System.out.println("Playercount set");
			}
		}catch(IOException e){
			Log.d("IO Error");
			e.printStackTrace();
		}
		return 0;
	}

	public String getMapStringFromServer() {
		try{
			String s = in.readLine();
			Log.d(s);
			return s;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	public boolean getPlayerFromServer(){
		try{
			String m = in.readLine();
			String[] arr = m.split(",");
			Log.d(m);

			if(arr[0].equals("player")){
				int i = Integer.parseInt(arr[1]);
				int x = Integer.parseInt(arr[2]);
				int y = Integer.parseInt(arr[3]);
				client.getMain().getRH().addServerCreatedPlayer(i, x, y);
				return true;
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public void flush(String s){
		out.println(s);
	}
}
