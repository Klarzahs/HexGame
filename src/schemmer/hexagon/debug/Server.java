package schemmer.hexagon.debug;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
 
public class Server implements Runnable {
 
    public final static String ADDRESS = "127.0.0.1";
    public final static int PORT = 8511;
    public final static long TIMEOUT = 10000;
     
    private ServerSocketChannel serverChannel;
    private Selector selector;
    //private Map<SocketChannel,byte[]> dataTracking = new HashMap<SocketChannel, byte[]>();
    private ArrayList<SocketChannel> clientChannels = new ArrayList<SocketChannel>();
 
    public Server(){
        init();
    }
 
    private void init(){
        System.out.println("SERVER: initializing server");
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
 
    @Override
    public void run() {
        System.out.println("SERVER: Now accepting connections...");
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
                        System.out.println("SERVER: Accepting connection");
                        accept(key);
                    }
                    if (key.isReadable()){
                        System.out.println("SERVER: Reading connection");
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
 
  /*  public void write(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        byte[] data = dataTracking.get(channel);
        dataTracking.remove(channel);
         
        channel.write(ByteBuffer.wrap(data));
         
        key.interestOps(SelectionKey.OP_READ);
    }*/

    private void closeConnection(){
        System.out.println("SERVER: Closing server down");
        if (selector != null){
            try {
                selector.close();
                serverChannel.socket().close();
                serverChannel.close();
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
        //byte[] hello = new String("SERVER: Hello from server").getBytes();
        //dataTracking.put(socketChannel, hello);
    }
 
    private void read(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        readBuffer.clear();
        int read;
        try {
            read = channel.read(readBuffer);
        } catch (IOException e) {
            System.out.println("SERVER: Reading problem, closing connection");
            key.cancel();
            channel.close();
            return;
        }
        if (read == -1){
            System.out.println("SERVER: Nothing was there to be read, closing connection");
            channel.close();
            key.cancel();
            return;
        }
        // IMPORTANT - don't forget the flip() the buffer. It is like a reset without clearing it.
        readBuffer.flip();
        byte[] data = new byte[1000];
        readBuffer.get(data, 0, read);
        System.out.println("SERVER: Received: "+new String(data));
    }
    
    public void sendMessage(SocketChannel c, String s){
		try {
			while(!c.isConnected()){}
			c.write(ByteBuffer.wrap(s.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public void sendMessage(String s){
		for(int i = 0; i < clientChannels.size(); i++){
			sendMessage(clientChannels.get(i), s);
		}
	}
 
}