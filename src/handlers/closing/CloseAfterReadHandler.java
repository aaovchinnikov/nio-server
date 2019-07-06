package handlers.closing;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;

import org.slf4j.Logger;

public class CloseAfterReadHandler
		implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel client;
	private final Logger logger;

	public CloseAfterReadHandler(AsynchronousSocketChannel client, Logger logger) {
		this.client = client;
		this.logger = logger;
	}

	@Override
	public void completed(Integer count, ByteBuffer buffer) {
		try {
			this.logger.info("Bytes read from socket {}", count);
			SocketAddress socket = client.getRemoteAddress();
			this.client.close();
			this.logger.info("Connection with {} closed", socket);
		} catch (IOException e) {
			this.logger.error("Exception on connection close", e);
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
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
