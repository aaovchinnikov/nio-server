package handlers.read;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import handlers.HandlerFactory;

public class EchoingReadHandlerFactory implements HandlerFactory {
	private final Logger logger;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	private final HandlerFactory factory;
	private final long writeTimeout;
	private final TimeUnit writeTimeUnit;
	
	public EchoingReadHandlerFactory(Logger logger, long readTimeout,
			TimeUnit readTimeUnit, HandlerFactory factory, long writeTimeout,
			TimeUnit writeTimeUnit) {
		this.logger = logger;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
		this.factory = factory;
		this.writeTimeout = writeTimeout;
		this.writeTimeUnit = writeTimeUnit;
	}

	@Override
	public CompletionHandler<Integer, ByteBuffer> createHandler(
			AsynchronousSocketChannel client) {
		return new EchoingReadHandler(client, new StringBuilder(),
				this.logger, this.readTimeout, this.readTimeUnit, 
				this.factory, this.writeTimeout, this.writeTimeUnit);
	}
}
