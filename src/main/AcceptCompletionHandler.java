package main;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	/** Size of ByteBuffers allocated for connections */
	private final int size;
	
	/**
	 * 
	 * @param size
	 */
	public AcceptCompletionHandler(int size) {
		this.size = size;
	}	
	
	@Override
	public void completed(AsynchronousSocketChannel client,	AsynchronousServerSocketChannel server) {
		System.out.println("Server: " + Thread.currentThread().getName()+ " accepted connection.");
		server.accept(server, this);
		ByteBuffer buffer = ByteBuffer.allocate(this.size);
        client.read(buffer, 0, TimeUnit.SECONDS, null, new ReadCompletionHandler(buffer, client, new StringBuilder()));
	}

	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel server) {
		System.out.println("Server: " + Thread.currentThread().getName()+ " failed accepting connection.");
		exc.printStackTrace();
	}
}
