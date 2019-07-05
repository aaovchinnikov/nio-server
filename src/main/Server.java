package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

import handlers.AcceptCompletionHandler;

public class Server {
	private final SocketAddress socket;
	/** Size of ByteBuffers allocated for connections */
	private final int size;

	
	public Server(SocketAddress socket, int size) {
		this.socket = socket;
		this.size = size;
	}
	
	public void start() throws IOException {
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 2);
		try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group)){
			server.bind(socket);
			server.accept(server, new AcceptCompletionHandler(this.size));
			synchronized (this) { // just wait as documented in javadoc for wait()
				while (true) {
					this.wait();
					System.out.println("Server: " + Thread.currentThread().getName() + ": main thread woke up");
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Server: " + Thread.currentThread().getName() + ": main thread interrupted");
		} finally {
			group.shutdownNow();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = new Server(new InetSocketAddress(8080), 500);
		server.start();
	}
}
