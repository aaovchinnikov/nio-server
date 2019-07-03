package main;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

public class Server {
	private final SocketAddress socket;

	
	public Server(SocketAddress socket) {
		this.socket = socket;
	}
	
	public void start() throws IOException, InterruptedException {
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 2);
		try { 
			AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
			server.bind(socket);
			while(true) {
// FIXME multiple calls to accept.
				server.accept(server, new AcceptCompletionHandler(500));
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
