package schemmer.hexagon.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import schemmer.hexagon.player.Player;

public class ServerThread extends Thread{
	private Server server;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;

	private int nr;

	private boolean clientReady = false;
	private boolean ack = false;
	private static final long ACK_TIMEOUT = 250l;

	public ServerThread(Server server, Socket sck, int i){
		try{
			this.server = server;
			nr = i;
			client = sck;
			client.setSendBufferSize( 256 * 1024 );
			client.setReceiveBufferSize( 256 * 1024 );
			client.setTcpNoDelay(true);

			out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));

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
				message = in.readLine();
				if(message != null){
					server.log("Received: "+message);
					if(message.equals("clientReady")){
						server.clientReady(nr);
						clientReady = true;
					}
					if(message.substring(0, "attack".length()).equals("attack"))
						server.attack(nr, message);
					if(message.substring(0, "move".length()).equals("move"))
						server.move(nr, message);
					if(message.substring(0, "nextPlayer".length()).equals("nextPlayer"))
						server.nextPlayer(nr);
					if(message.substring(0, "ack".length()).equals("ack"))
						this.ack();

					message = null;
				} else{
					ServerThread.sleep(100);
				}
				//ServerThread.yield();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void sendMap(){
		String message = server.getMH().getMapAsChar();
		String m = "map,";
		m += server.getMH().RADIUS+"";
		flush(m);
		flush(message);
	}

	public void sendPlayers(int count){
		while(!clientReady){
			for(int p = 0; p < count; p++){
				sendPlayer(server.getRH().getPlayer(p), p);
			}
			try{
				ServerThread.sleep(50);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void sendPlayer(Player pl, int i){
		// ack is handled by clientReady
		out.write("player");
		out.write(i);
		out.write(pl.getStartingPosition().getX());
		out.write(pl.getStartingPosition().getY());
		out.flush();
	}

	public void confirmAttack(int x, int y, int ex, int ey, float dmgToYou, float dmgToEnemy){
		String m;
		ack = false;
		while(!ack){
			m = "attackConfirm,";
			m += x+",";
			m += y+",";
			m += ex+",";
			m += ey+",";
			m += dmgToYou+",";
			m += dmgToEnemy+"";
			flush(m);
			try{
				ServerThread.sleep(ACK_TIMEOUT);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void confirmMove(int fx, int fy, int tx, int ty){
		String m;
		ack = false;
		while(!ack){
			server.log("To ("+nr + "): move Confirm");
			m = "moveConfirm,";
			m += fx+",";
			m += fy+",";
			m += tx+",";
			m += ty+"";
			flush(m);
			try{
				ServerThread.sleep(ACK_TIMEOUT);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void declineMove(int fx, int fy, int tx, int ty){
		String m;
		ack = false;
		while(!ack){
			m = "moveDecline,";
			m += fx+",";
			m += fy+",";
			m += tx+",";
			m += ty+"";
			flush(m);
			try{
				ServerThread.sleep(ACK_TIMEOUT);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void nextPlayer(){
		ack = false;
		while(!ack){
			flush("nextPlayer");
			try{
				ServerThread.sleep(ACK_TIMEOUT);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void flush(String m){
		try {
			out.println(m);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ack(){
		ack = true;
	}
}
