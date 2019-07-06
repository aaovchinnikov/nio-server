package handlers.read;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;

import handlers.HandlerFactory;

public class LoggingReadUntilExitHandlerFactory implements HandlerFactory {
	private final Logger logger;
	
	public LoggingReadUntilExitHandlerFactory(Logger logger) {
		this.logger = logger;
	}

	@Override
	public CompletionHandler<Integer, ByteBuffer> createHandler(
			AsynchronousSocketChannel client) {
		return new LoggingReadUntilExitHandler(client, new StringBuilder(), this.logger);
	}

}
