package schemmer.hexagon.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.handler.RoundHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.player.Player;
import schemmer.hexagon.units.Unit;
import schemmer.hexagon.utils.Log;

public class Server extends Main implements Runnable{
	private int maxPlayerCount;
	private int maxAICount;
	private volatile int clientReady = 0;
	private volatile int clientConnected = 0;

	private final static int PLAYER_COUNT = 2;
	private final static int AI_COUNT = 0;

	private ServerWindow window;

	public final static String ADDRESS = "127.0.0.1";
	public final static int PORT = 5555;
	public final static long TIMEOUT = 10000;

	private ServerSocketChannel serverChannel;
	private Selector selector; 
	private ByteBuffer buf = ByteBuffer.allocate(256);
	
	private ArrayList<SocketChannel> clientChannels = new ArrayList<SocketChannel>();
	private ServerFunctions functions = new ServerFunctions(this);

	public Server(int player, int ai) throws IOException{
		createUI();

		this.maxPlayerCount = player;
		this.maxAICount = ai;
		
		init();		//handles accepting implictly on run
	}

	private void init(){
		log("Initializing server");
		// We do not want to call init() twice and recreate the selector or the serverChannel.
		if (selector != null) return;
		if (serverChannel != null) return;

		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT));

			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finishInit(){
		eh = new EntityHandler();
		mh = new MapHandler(this);

		rh = new RoundHandler(mh);
		rh.createAllPlayers(maxPlayerCount, maxAICount);

		sendMaps();
		sendPlayerCount();
		sendPlayers();
		
		log("Finished initializing");

		//while(!clientsReady()){}		// wait
		log("Game started");
		rh.startRound();
	}

	
	@Override
	public void run() {
		log("Now accepting connections...");
		try{
			// A run the server as long as the thread is not interrupted.
			while (!Thread.currentThread().isInterrupted()){		
				selector.select(TIMEOUT);

				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()){
					SelectionKey key = keys.next();
					keys.remove();

					if (!key.isValid()){
						continue;
					}
					if (key.isAcceptable()){
						log("Accepting connection");
						accept(key);
					}
					if (key.isReadable()){
						read(key);
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally{
			closeConnection();
		}

	}

	
	private void closeConnection(){
		log("Closing server down");
		if (selector != null){
			try {
				selector.close();
				//serverChannel.socket().close();
				//serverChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void accept(SelectionKey key) throws IOException{
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);

		clientChannels.add(socketChannel);

		socketChannel.register(selector, SelectionKey.OP_READ);
		log("Accepted client at "+socketChannel.getRemoteAddress() +", valid Ops:"+ socketChannel.validOps());
		clientConnected++;
		if(clientConnected == maxPlayerCount){
			finishInit();
		}
	}

	private void read(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();

		//readBuffer.clear();
		int read;
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		try {
			read = channel.read(readBuffer);
		} catch (IOException e) {
			log("Reading problem, closing connection");
			key.cancel();
			channel.close();
			return;
		}
		if (read == -1){
			log("Nothing was there to be read, closing connection");
			channel.close();
			key.cancel();
			return;
		}
		readBuffer.flip();
		byte[] buff = new byte[1024];
		readBuffer.get(buff, 0, read);
		String[] stringArr = (new String(buff)).split("/");
		parseMessage(stringArr, channel);
	}

	private void parseMessage(String[] msgs, SocketChannel channel){
		//get the nr from the client
		int nr = clientChannels.indexOf(channel);
		for(int i = 0; i < msgs.length; i++){
			String message = truncateMessage(msgs[i]);
			if(message != null){
				this.log("Received: "+message);
				if(message.equals("clientReady"))
					this.clientReady();
				if(message.substring(0, "attack".length()).equals("attack"))
					this.attack(nr, message);
				if(message.substring(0, "move".length()).equals("move"))
					this.move(nr, message);
				if(message.substring(0, "nextPlayer".length()).equals("nextPlayer"))
					this.nextPlayer(nr);
				message = null;
			} 
		}
	}
	
	private String truncateMessage(String msg){
		int end = -1;
		for(int i = 0; i < msg.length(); i++){
			if((int) msg.charAt(i) == 0){		// ASCII: NULL, empty String
				end = i;
				break;
			}
		}
		if(end != -1)
			return msg.substring(0, end);
		return msg;
	}
	
	
	public void broadcast(String msg) throws IOException {
		ByteBuffer msgBuf=ByteBuffer.wrap((msg+"/").getBytes());
		for(SelectionKey key : selector.keys()) {
			if(key.isValid() && key.channel() instanceof SocketChannel) {
				SocketChannel sch=(SocketChannel) key.channel();
				sch.write(msgBuf);
				msgBuf.rewind();
			}
		}
	}

	public void append(String s){
		window.log(s);
	}

	public void log(String s){
		window.log(s);
		window.newLine();
	}

	public void sendMaps(){
		for(int i = 0; i < clientChannels.size(); i++){
			functions.sendMap(clientChannels.get(i));
		}
	}
	
	public void sendPlayers(){
		for(int i = 0; i < clientChannels.size(); i++){
			for(int p = 0; p < maxPlayerCount; p++){
				functions.sendPlayer(clientChannels.get(i),  rh.getPlayer(p), p);
			}
		}
	}

	public void sendPlayerCount(){
		for(int i = 0; i < clientChannels.size(); i++){
			sendPlayerCount(i);
		}
	}

	public void nextPlayer(){
		//TODO: 
	}

	public void sendPlayerCount(int i){
		String m = "playerCount,"+maxPlayerCount+","+maxAICount;
		functions.sendMessage(clientChannels.get(i), m);
	}

	public void clientReady(){
		clientReady++;
		log("Clients ready: "+ clientReady);
	}

	private void createUI(){
		window = new ServerWindow();
		window.setSize(800, 640);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}//windowClosing
		});
		window.setVisible(true);
	}
	
	public void attack(int nr, String m){
		String[] arr = m.split(",");
		int x = Integer.parseInt(arr[1]);
		int y = Integer.parseInt(arr[2]);
		int ex = Integer.parseInt(arr[3]);
		int ey = Integer.parseInt(arr[4]);
		Hexagon fhex = mh.getMap()[x][y];
		Hexagon ehex = mh.getMap()[ex][ey];
		Unit friend = fhex.getUnit();
		Unit enemy = ehex.getUnit();
		if(friend == null || enemy == null){
			log("State missmatch, attack canceled!");
			System.err.println("State missmatch, attack canceled!");
		}
		float dmgToEnemy = friend.getAttack() - enemy.getDefense() * ehex.getMovementCosts() / 2f;
		float dmgToYou = enemy.getAttack() - friend.getDefense() * fhex.getMovementCosts() / 2f;
		friend.setHealth(friend.getHealth() - (int)(dmgToYou * 10));
		enemy.setHealth(enemy.getHealth() - (int)(dmgToEnemy * 10));
		confirmAttack(nr, x, y, ex, ey, dmgToYou, dmgToEnemy);
	}

	public void confirmAttack(int nr, int x, int y, int ex, int ey, float dmgToYou, float dmgToEnemy){
		for(int i = 0; i < clientChannels.size(); i++){
			if(i != nr)
				functions.confirmAttack(clientChannels.get(i), x, y, ex, ey, dmgToYou, dmgToEnemy);
		}
	}

	public void move(int nr, String m){
		//if(clientsReady()){
			String[] arr = m.split(",");
			int fx = Integer.parseInt(arr[1]);
			int fy = Integer.parseInt(arr[2]);
			int tx = Integer.parseInt(arr[3]);
			int ty = Integer.parseInt(arr[4]);
			try{
				log("Move: "+fx+" "+fy+" to "+tx+" "+ty+" ("+nr+")");
				Hexagon from = mh.getMap()[fx][fy];
				Hexagon to = mh.getMap()[tx][ty];
				Unit u = from.getUnit();
				to.moveTo(u);
				confirmMove(nr, fx, fy, tx, ty);
			}catch(Exception e){
				log(e.getMessage());
				declineMove(nr, fx, fy, tx, ty);
			}
	//	}
	}

	public void confirmMove(int nr, int fx, int fy, int tx, int ty){
		for(int i = 0; i < clientChannels.size(); i++){
			if(i != nr)
				functions.confirmMove(clientChannels.get(i), fx, fy, tx, ty);
		}
	}

	public void declineMove(int nr, int fx, int fy, int tx, int ty){
		functions.declineMove(clientChannels.get(nr), fx, fy, tx, ty);
	}

	public void nextPlayer(int nr){
		for(int i = 0; i < clientChannels.size(); i++){
			if(i != nr)
				functions.nextPlayer(clientChannels.get(i));
		}
	}

	private boolean clientsReady(){
		return (clientReady >= maxPlayerCount);
	}
	
	public int getMaxCount(){
		return (maxAICount + maxPlayerCount);
	}
	
	public static void main (String [] args) {
		try{
			Server server = new Server(PLAYER_COUNT, AI_COUNT);
			Thread threadServer = new Thread(server);
			threadServer.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
