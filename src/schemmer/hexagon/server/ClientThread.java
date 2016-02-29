package schemmer.hexagon.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Log;

public class ClientThread extends Thread{
	private Client client;
	private DataOutputStream out;
	private DataInputStream in;
	
	public ClientThread(Client c, DataOutputStream out, DataInputStream in){
		this.client = c;
		this.out = out;
		this.in = in;
		Log.d("ClientThread created");
	}
	
	@Override
	public void run(){
		String message;
		try{
			while(true){
				if(in.available() > 0){
					message = in.readUTF();
					if(message.equals("attackConfirm"))
						confirmAttack(in.readInt(), in.readInt(),in.readInt(), in.readInt(), in.readFloat(), in.readFloat());
					if(message.equals("moveConfirm"))
						confirmMove(in.readInt(), in.readInt(),in.readInt(), in.readInt());
					if(message.equals("moveDecline"))
						declineMove(in.readInt(), in.readInt(),in.readInt(), in.readInt());
					if(message.equals("nextPlayer"))
						nextPlayer();
				} else{
					ClientThread.sleep(50);
				}
				ClientThread.yield();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void reply(String message){
		try{
			out.writeUTF(message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void confirmAttack(int x, int y, int ex, int ey, float dmgToYou, float dmgToEnemy){
		MapHandler mh = client.getMain().getMH();
		Hexagon fhex = mh.getMap()[x][y];
		Hexagon ehex = mh.getMap()[ex][ey];
		Unit friend = fhex.getUnit();
		Unit enemy = ehex.getUnit();
		friend.setHealth(friend.getHealth() - (int)(dmgToYou * 10));
		enemy.setHealth(enemy.getHealth() - (int)(dmgToEnemy * 10));
	}
	
	public void confirmMove(int fx, int fy, int tx, int ty){
		Log.d("received confirm");
		Hexagon from = client.getMain().getMH().getMap()[fx][fy];
		Hexagon to = client.getMain().getMH().getMap()[tx][ty];
		Unit u = from.getUnit();
		Log.d("Unit is "+(u != null));
		to.moveToLocal(u);
		from.unitMoved();
	}
	
	//TODO: fix movement cost
	public void declineMove(int fx, int fy, int tx, int ty){
		Hexagon from = client.getMain().getMH().getMap()[fx][fy];
		Hexagon to = client.getMain().getMH().getMap()[tx][ty];
		Unit u = to.getUnit();
		from.moveTo(u);
		to.unitMoved();
	}
	
	public void nextPlayer(){
		client.getMain().getRH().nextPlayerLocal();
	}
}
