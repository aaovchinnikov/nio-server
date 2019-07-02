package main;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Server {
	private final SocketAddress socket;
	private final Semaphore semaphore;

	
	public Server(SocketAddress socket, Semaphore semaphore) {
		this.socket = socket;
		this.semaphore = semaphore;
	}
	
	public void start() throws IOException, InterruptedException {
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 2);
		try { 
			AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
			server.bind(socket);
			while(true) {
				// CompletionHandler is inner anonymous class? It seems to be the reason why it has access to outer class fields.
			    server.accept(null, new CompletionHandler<AsynchronousSocketChannel,Object>() {
			    	@Override
	    	        public void completed(AsynchronousSocketChannel result, Object attachment) {
	    	            // we have access to field "server" and create another accept-request to OS in thread pool.
			    		// all the time we have one accepting thread in thread pool
			    		if (server.isOpen()){
	    	                server.accept(null, this);
	    	            }
	    	 
	    	            clientChannel = result;
	    	            if ((clientChannel != null) && (clientChannel.isOpen())) {
	    	                ReadWriteHandler handler = new ReadWriteHandler();
	    	                ByteBuffer buffer = ByteBuffer.allocate(32);
	    	 
	    	                Map<String, Object> readInfo = new HashMap<>();
	    	                readInfo.put("action", "read");
	    	                readInfo.put("buffer", buffer);
	    	 
	    	                clientChannel.read(buffer, readInfo, handler);
	    	             }
	    	         }
	    	         @Override
	    	         public void failed(Throwable exc, Object attachment) {
	    	             // process error
	    	         }
	 
	    	    });			
				
				
				
				this.semaphore.acquire();
				server.accept(this.semaphore, new AcceptCompletionHandler());
				if(Thread.currentThread().isInterrupted()) {
					group.shutdownNow();
					break;
				}
			}
		} finally {
			group.shutdownNow();
		}
	}
	
}
