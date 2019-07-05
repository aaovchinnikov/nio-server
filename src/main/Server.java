package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import handlers.AcceptCompletionHandler;

public class Server {
	private final SocketAddress socket;
	/** Size of ByteBuffers allocated for connections */
	private final int size;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	private final long writeTimeout;
	private final TimeUnit writeTimeUnit;

	public Server(SocketAddress socket, int size, long readTimeout,
			TimeUnit readTimeUnit, long writeTimeout, TimeUnit writeTimeUnit) {
		this.socket = socket;
		this.size = size;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
		this.writeTimeout = writeTimeout;
		this.writeTimeUnit = writeTimeUnit;
	}

	/**
	 * Constructs Server instance with infinite read and write timeouts
	 * @param socket
	 * @param size
	 */
	public Server(SocketAddress socket, int size) {
		this(socket,size, 0, TimeUnit.SECONDS, 0, TimeUnit.SECONDS);
	}
	
	public void start() throws IOException {
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 2);
		try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group)){
			server.bind(socket);
			server.accept(server, new AcceptCompletionHandler(this.size,this.readTimeout, this.readTimeUnit, this.writeTimeout, this.writeTimeUnit));
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
