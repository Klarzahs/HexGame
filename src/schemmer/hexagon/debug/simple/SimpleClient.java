package schemmer.hexagon.debug.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
 
public class SimpleClient implements Runnable{
	private InetAddress address;
	private int port;
	
	private SocketChannel channel;
	private ReadableByteChannel wrappedChannel;
   
	public SimpleClient(InetAddress ia, int p){
		address = ia;
		port = p;
		connect();
	}
	
	public void connect(){
		try{
			InetSocketAddress hostAddress = new InetSocketAddress(address, port);
		    channel = SocketChannel.open(hostAddress);
		    channel.socket().setSoTimeout(10);
		    InputStream inStream = channel.socket().getInputStream();
		    wrappedChannel = Channels.newChannel(inStream);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
    public void send(String msg){
    	try{
	        byte [] message = msg.getBytes();
	        ByteBuffer buffer = ByteBuffer.wrap(message);
	        channel.write(buffer);
	        buffer.clear();
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
				wrappedChannel.close();
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
    	}catch(SocketTimeoutException e){
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
	
	@Override
	public void run(){
		while(true){
			try{
				this.read();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
    public static void main(String[] args) throws Exception {
    	SimpleServer server = new SimpleServer(12345);
    	Thread t = new Thread(server);
    	t.start();
        SimpleClient sc = new SimpleClient(null, 12345);
        Thread tc = new Thread(sc);
        tc.start();
        SimpleClient sc2 = new SimpleClient(null, 12345);
        Thread tc2 = new Thread(sc2);
        tc2.start();
        int i = 0;
        while(true){
        	sc.send("Hello to server");
        	sc2.send("Hello to server");
        	if(i % 10 == 0)
        		server.broadcast("Hallöööööle");
        	i++;
        }
    }
}