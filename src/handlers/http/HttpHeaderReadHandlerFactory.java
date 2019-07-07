package handlers.http;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import handlers.HandlerFactory;
import resources.Resource;

public class HttpHeaderReadHandlerFactory implements HandlerFactory {
	private final Resource resource;

	public HttpHeaderReadHandlerFactory(Resource resource) {
		this.resource = resource;
	}

	@Override
	public CompletionHandler<Integer, ByteBuffer> createHandler(
			AsynchronousSocketChannel client) {
		return new HttpHeaderReadHandler(client, new StringBuilder(), this.resource, Operation.READ);
	}

}
