package handlers.closing;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;

import handlers.HandlerFactory;

public class CloseAfterReadHandlerFactory implements HandlerFactory {
	private final Logger logger;
	
	public CloseAfterReadHandlerFactory(Logger logger) {
		this.logger = logger;
	}

	@Override
	public CompletionHandler<Integer, ByteBuffer> createHandler(
			AsynchronousSocketChannel client) {
		return new CloseAfterReadHandler(client, this.logger);
	}

}
