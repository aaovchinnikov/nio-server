package handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	/** Size of ByteBuffers allocated for connections */
	private final int size;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	private final long writeTimeout;
	private final TimeUnit writeTimeUnit;
	private final Logger logger;
	
	/**
	 * 
	 * @param size
	 */
	public AcceptCompletionHandler(int size, long readTimeout, TimeUnit readTimeUnit,
			long writeTimeout, TimeUnit writeTimeUnit, Logger logger) {
		this.size = size;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
		this.writeTimeout = writeTimeout;
		this.writeTimeUnit = writeTimeUnit;
		this.logger = logger;
	}	
	
	@Override
	public void completed(AsynchronousSocketChannel client,	AsynchronousServerSocketChannel server) {
		try {
			logger.info("Server: {}: accepted connection from {}.", Thread.currentThread().getName(), client.getRemoteAddress());
		server.accept(server, this); // async call for new connection acceptance and handling with this handler code
		ByteBuffer buffer = ByteBuffer.allocate(this.size);
        client.read(buffer, this.readTimeout, this.readTimeUnit, null, 
        		new ReadCompletionHandler(buffer, client, new StringBuilder(), 
        				this.readTimeout, this.readTimeUnit, 
        				this.writeTimeout, this.writeTimeUnit));
		} catch (IOException e) {
			logger.error("Server: can't get client-socket remote address", e);
		}
	}

	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel server) {
		if (exc instanceof AsynchronousCloseException) {
			this.logger.info("Server: {}: accept handler stopped due server socket shutdown", Thread.currentThread().getName());
		} else {
			logger.error("Server: {}: failed accepting connection.",Thread.currentThread().getName());
			logger.error("Thrown exception is", exc);
		}
	}
}
