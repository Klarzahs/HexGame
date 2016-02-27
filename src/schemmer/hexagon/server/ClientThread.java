package schemmer.hexagon.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.units.Unit;

public class ClientThread extends Thread{
	private Client client;
	private DataOutputStream out;
	private DataInputStream in;
	
	public ClientThread(Client c, DataOutputStream out, DataInputStream in){
		this.client = c;
		this.out = out;
		this.in = in;
	}
	
	@Override
	public void run(){
		String message;
		try{
			while(true){
				if(in.available() > 0){
					message = in.readUTF();
					if(message.equals("confirmAttack")){
						confirmAttack(in.readInt(), in.readInt(),in.readInt(), in.readInt(), in.readFloat(), in.readFloat());
					}
				}
			}
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
}
