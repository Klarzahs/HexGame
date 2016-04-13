package schemmer.hexagon.debug;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;


public class Client implements Runnable {

	private ArrayList<String> messages = new ArrayList<String>();
	private Selector selector;

	private SocketChannel channel;


	public Client(String message){
		messages.add(message);
		try{
			selector = Selector.open();
			channel = SocketChannel.open();
			channel.configureBlocking(false);

			channel.register(selector, SelectionKey.OP_CONNECT);
			channel.connect(new InetSocketAddress("127.0.0.1", 8511));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()){

				selector.select(1000);

				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()){
					SelectionKey key = keys.next();
					keys.remove();

					if (!key.isValid()) continue;

					if (key.isConnectable()){
						System.out.println("CLIENT: I am connected to the server");
						connect(key);
					}   
					if (key.isWritable()){
						write(key);
					}
					if (key.isReadable()){
						read(key);
					}
				}   
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			close();
		}
	}

	private void close(){
		try {
			selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void read (SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(1000);
		readBuffer.clear();
		int length;
		try{
			length = channel.read(readBuffer);
		} catch (IOException e){
			System.out.println("CLIENT: Reading problem, closing connection");
			key.cancel();
			channel.close();
			return;
		}
		if (length == -1){
			System.out.println("CLIENT: Nothing was read from server");
			channel.close();
			key.cancel();
			return;
		}
		readBuffer.flip();
		byte[] buff = new byte[1024];
		readBuffer.get(buff, 0, length);
		
		//channel.register(selector, SelectionKey.OP_READ);
		
		System.out.println("CLIENT("+messages.get(0)+"): Server said: "+new String(buff));
	}

	private void write(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		if(!messages.isEmpty()){
			String message = messages.get(0);
			channel.write(ByteBuffer.wrap(message.getBytes()));
		}

		channel.register(selector, SelectionKey.OP_READ);
	}

	private void connect(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		if (channel.isConnectionPending()){
			channel.finishConnect();
		}
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}
	
	public void sendMessage(String s){
		try {
			while(!channel.isConnected()){}
			channel.write(ByteBuffer.wrap((s+messages.get(0)).getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		Client client = new Client("C1");
		Client client2 = new Client("C2");
		Server server = new Server();
		Thread threadServer = new Thread(server);
		Thread threadClient = new Thread(client);
		Thread threadClient2 = new Thread(client2);
		threadServer.start();
		threadClient.start();
		threadClient2.start();
		for(int i = 0; i < 10; i++){
			client.sendMessage("TEST MESSAGE "+i);
			client2.sendMessage("TEST MESSAGE "+i);
		}
		server.sendMessage("Hello to all clients");
		//thread2.start();
	}


}