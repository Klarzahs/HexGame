package schemmer.hexagon.server;

import java.util.ArrayList;

import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.processes.MapFactory;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Dijkstra;
import schemmer.hexagon.utils.Log;

public class ClientFunctions{
	private Client client;

	public ClientFunctions(Client c){
		this.client = c;
	}

	public void handleMessage(String m){
		if(m != null){
			Log.d("ClientFunctions: received "+m);
			String messages[] = m.split("/");
			for(int i = 0; i < messages.length; i++){
				String message = messages[i];
				if(message.substring(0, "map".length()).equals("map"))
					getMapFromServer(message);
				if(message.substring(0, "playerCount".length()).equals("playerCount"))
					setPlayerCount(message);
				else if(message.substring(0, "player".length()).equals("player"))
					getPlayerFromServer(message);
				if(message.substring(0, "attackConfirm".length()).equals("attackConfirm"))
					confirmAttack(message);
				if(message.substring(0, "moveConfirm".length()).equals("moveConfirm"))
					confirmMove(message);
				if(message.substring(0, "moveDecline".length()).equals("moveDecline"))
					declineMove(message);
				if(message.substring(0, "nextPlayer".length()).equals("nextPlayer"))
					nextPlayer();
			}
		} 
	}

	public void reply(String message){
		try{
			client.send(message);
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
		//update visibility
		ArrayList<Hexagon> path = Dijkstra.getMovementPath(client.getMain().getMH().getMap(), client.getMain().getMH(), from, to);
		int costs = Dijkstra.getMovementCost(client.getMain().getMH().getMap(), client.getMain().getMH(), from, to);
		client.getMain().getMH().updateVisibleMap(path, client.getMain().getCurrentPlayer(), u);
		//actually move
		to.moveToLocal(u);
		//update references
		u.moved(costs);
		from.unitMoved();
		client.getMain().getMH().clearMovementRange();
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
		to.moveToLocal(u);
		from.unitMoved();
	}

	public void nextPlayer(){
		client.getMain().getRH().nextPlayerLocal();
	}

	public void sendNextPlayer(){
		client.send("nextPlayer");
	}

	public void setPlayerCount(String s) {
		String[] arr = s.split(",");
		if(arr[0].equals("playerCount")){
			client.getMain().getRH().setMaxPlayers(Integer.parseInt(arr[1]));
			client.getMain().getRH().setMaxAIs(Integer.parseInt(arr[2]));
		}
	}

	public void getMapFromServer(String s) {
		int radius = -1;

		String[] m = s.split(",");

		if(!m[0].equals("map")){
			return;
		}

		radius = Integer.parseInt(m[1]);
		if(s.equals("") || radius == -1){
			System.out.println("Failed to get mapradius");
			return;
		}

		MapFactory.setMapSeed(m[2]);
		MapFactory.setBiomeSeed(m[3]);
		client.getMain().getMH().createHexagon(radius);

		client.getMain().receivedMap = true;
	}

	public boolean getPlayerFromServer(String m){
		try{
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
		Log.d("Sending: "+s);
		client.send(s);
	}
}
