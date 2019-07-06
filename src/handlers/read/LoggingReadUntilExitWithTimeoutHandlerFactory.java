package handlers.read;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import handlers.HandlerFactory;

public class LoggingReadUntilExitWithTimeoutHandlerFactory implements HandlerFactory {
	private final Logger logger;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	
	public LoggingReadUntilExitWithTimeoutHandlerFactory(Logger logger,
			long readTimeout, TimeUnit readTimeUnit) {
		this.logger = logger;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
	}

	@Override
	public CompletionHandler<Integer, ByteBuffer> createHandler(
			AsynchronousSocketChannel client) {
		return new LoggingReadUntilExitWithTimeoutHandler(client, new StringBuilder(),
				this.logger, this.readTimeout, this.readTimeUnit);
	}
}
