package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handlers.accept.ReadingWithTimeoutLoggingAcceptHandler;
import handlers.http.HttpHeaderReadHandlerFactory;
import resources.DispatchingResource;

public class Server {
	private final SocketAddress socket;
	private final Logger logger;
	private final CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> acceptHandler;

	/**
	 * Supposes {@link #acceptHandler} should be reused if multiple connections would be served
	 * @param socket
	 * @param logger
	 * @param acceptHandler
	 */
	public Server(SocketAddress socket, Logger logger, 
			CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> acceptHandler) {
		this.socket = socket;
		this.logger = logger;
		this.acceptHandler = acceptHandler;
	}
	
	public void start() throws IOException {
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 2);
		try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group)){
			server.bind(socket);
			server.accept(server, this.acceptHandler);
			this.logger.info("Server started");
			synchronized (this) { // just wait as documented in javadoc for wait()
				while (true) {
					this.wait();
					this.logger.debug("Server: {}: main thread woke up", Thread.currentThread().getName());
				}
			}
		} catch (InterruptedException e) {
			this.logger.info("Server: {}: main thread interrupted", Thread.currentThread().getName());
		} finally {
			group.shutdownNow();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Logger logger = LoggerFactory.getLogger(Server.class);
		CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> acceptHandler = 
				new ReadingWithTimeoutLoggingAcceptHandler(500, 
						new HttpHeaderReadHandlerFactory(new DispatchingResource()),
						logger, 
						0, TimeUnit.SECONDS);
		Server server = new Server(new InetSocketAddress(8080), logger, acceptHandler);
		logger.info("Starting the server...");
		server.start();
	}
}
