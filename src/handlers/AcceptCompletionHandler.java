package handlers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	/** Size of ByteBuffers allocated for connections */
	private final int size;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	private final long writeTimeout;
	private final TimeUnit writeTimeUnit;
	
	/**
	 * 
	 * @param size
	 */
	public AcceptCompletionHandler(int size, long readTimeout, TimeUnit readTimeUnit,
			long writeTimeout, TimeUnit writeTimeUnit) {
		this.size = size;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
		this.writeTimeout = writeTimeout;
		this.writeTimeUnit = writeTimeUnit;
	}	
	
	@Override
	public void completed(AsynchronousSocketChannel client,	AsynchronousServerSocketChannel server) {
		System.out.println("Server: " + Thread.currentThread().getName()+ ": accepted connection.");
		server.accept(server, this); // async call for new connection acceptance and handling with this handler code
		ByteBuffer buffer = ByteBuffer.allocate(this.size);
        client.read(buffer, this.readTimeout, this.readTimeUnit, null, 
        		new ReadCompletionHandler(buffer, client, new StringBuilder(), 
        				this.readTimeout, this.readTimeUnit, 
        				this.writeTimeout, this.writeTimeUnit));
	}

	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel server) {
		if (exc instanceof AsynchronousCloseException) {
			System.out.println("Server: " + Thread.currentThread().getName()+ ": accept handler stopped due server socket shutdown");
		} else {
			System.out.println("Server: " + Thread.currentThread().getName()+ ": failed accepting connection.");
			exc.printStackTrace();			
		}
	}
}
