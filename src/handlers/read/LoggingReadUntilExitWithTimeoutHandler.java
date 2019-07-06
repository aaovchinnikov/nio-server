package handlers.read;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * Reads from socket until "exit" substring is reached or timeout exceeds
 */
public class LoggingReadUntilExitWithTimeoutHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel client;
	private final StringBuilder builder;
	private final Logger logger;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	
	public LoggingReadUntilExitWithTimeoutHandler(
			AsynchronousSocketChannel client, StringBuilder builder,
			Logger logger, long readTimeout, TimeUnit readTimeUnit) {
		this.client = client;
		this.builder = builder;
		this.logger = logger;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
	}

	@Override
	public void completed(Integer count, ByteBuffer buffer) {
		this.logger.info("Server: {}: read {} bytes from connection", Thread.currentThread().getName(), count);
		if (count == -1) {
			logger.info("Server: {}: connection closed on end-of-stream reached.", Thread.currentThread().getName());
			try {
				this.client.close();
			} catch (IOException e) {
				this.logger.error("Exception on connection close", e);
			}
		} else {
			buffer.flip();
			this.builder.append(StandardCharsets.US_ASCII.decode(buffer));
			this.logger.debug("builder content: {}",builder.toString());
			buffer.clear();
			int position = this.builder.indexOf("exit");
			if(position != -1) {
				try {
					SocketAddress socket = this.client.getRemoteAddress();
					client.close();
					this.logger.info("Connection with {} closed due \"exit\" reached", socket);
				} catch (IOException e) {
					this.logger.error("Exception on connection close", e);
				}
			} else { // continue to read and handle with this handler
				this.client.read(buffer, readTimeout, readTimeUnit, buffer, this);				
			}
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer buffer) {
		if (exc instanceof AsynchronousCloseException) {
			this.logger.info("Server: {}: read handler stopped due server socket shutdown", Thread.currentThread().getName());
		} else if (exc instanceof InterruptedByTimeoutException) {
			try {
				SocketAddress socket = this.client.getRemoteAddress();
				this.client.close();
				this.logger.info("Connection with {} closed due read timeout", socket);
			} catch (IOException e) {
				this.logger.error("Exception on connection close", e);
			}
		} else {
			try {
				this.logger.error("Failed to read from socket", exc);
				SocketAddress socket = this.client.getRemoteAddress();
				this.client.close();
				this.logger.info("Connection with {} closed", socket);
			} catch (IOException e) {
				this.logger.error("Exception on connection close", e);
			}
		}
	}
}
