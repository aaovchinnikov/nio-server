package handlers.accept;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import handlers.HandlerFactory;

/**
 * Handler called after connection was accepted by {@link AsynchronousServerSocketChannel#accept()}.
 * Creates new {@link ByteBuffer} for accepted connection, creates new {@link CompletionHandler} instance by calling {@link HandlerFactory#createHandler(AsynchronousSocketChannel)} of supplied
 * {@link HandlerFactory} and uses this handler in {@link AsynchronousSocketChannel#read(ByteBuffer, long, TimeUnit, Object, CompletionHandler)} call.
 * {@link #readTimeout} and {@link #readTimeUnit} used only for first read operation. Further reads should be handled by other handlers objects, this object handles only first read.
 * @author sansey
 *
 */
public class ReadingWithTimeoutLoggingAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	/** Size of ByteBuffers allocated for connections */
	private final int size;
	private final HandlerFactory factory;
	private final Logger logger;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	
	/**
	 * 
	 * @param size
	 */
	public ReadingWithTimeoutLoggingAcceptHandler(int size, HandlerFactory factory, Logger logger,
			long readTimeout, TimeUnit readTimeUnit) {
		this.size = size;
		this.factory = factory;
		this.logger = logger;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
	}	
	
	@Override
	public void completed(AsynchronousSocketChannel client,	AsynchronousServerSocketChannel server) {
		try {
			logger.info("Server: {}: accepted connection from {}.", Thread.currentThread().getName(), client.getRemoteAddress());
			server.accept(server, this); // async call for new connection acceptance and handling with this handler code
			ByteBuffer buffer = ByteBuffer.allocate(this.size);
	        client.read(buffer, this.readTimeout, this.readTimeUnit, buffer, this.factory.createHandler(client)); 
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
