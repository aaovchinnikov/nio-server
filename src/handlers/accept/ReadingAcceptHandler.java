package handlers.accept;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import handlers.HandlerFactory;

/**
 * Handler called after connection was accepted by {@link AsynchronousServerSocketChannel#accept()}.
 * Creates new {@link ByteBuffer} for accepted connection, creates new {@link CompletionHandler} instance by calling {@link HandlerFactory#createHandler(AsynchronousSocketChannel)} of supplied
 * {@link HandlerFactory} and uses this handler in {@link AsynchronousSocketChannel#read(ByteBuffer, Object, CompletionHandler)} call.
 * @author sansey
 *
 */
public class ReadingAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	/** Size of ByteBuffers allocated for connections */
	private final int size;
	private final HandlerFactory factory;
	
	/**
	 * 
	 * @param size
	 */
	public ReadingAcceptHandler(int size, HandlerFactory factory) {
		this.size = size;
		this.factory = factory;
	}	
	
	@Override
	public void completed(AsynchronousSocketChannel client,	AsynchronousServerSocketChannel server) {
		server.accept(server, this); // async call for new connection acceptance and handling with this handler code
		ByteBuffer buffer = ByteBuffer.allocate(this.size);
        client.read(buffer, buffer, factory.createHandler(client)); 
	}

	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel server) {
	}
}
