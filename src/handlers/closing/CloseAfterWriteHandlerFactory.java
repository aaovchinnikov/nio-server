package handlers.closing;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;

import handlers.HandlerFactory;

public class CloseAfterWriteHandlerFactory implements HandlerFactory {
	private final Logger logger;
	
	public CloseAfterWriteHandlerFactory(Logger logger) {
		this.logger = logger;
	}

	@Override
	public CompletionHandler<Integer, ByteBuffer> createHandler(
			AsynchronousSocketChannel client) {
		return new CloseAfterWriteHandler(client, this.logger);
	}

}
