package handlers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public interface HandlerFactory {
	public CompletionHandler<Integer, ByteBuffer> createHandler(AsynchronousSocketChannel client);
}
