package handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ReadCompletionHandler implements CompletionHandler<Integer, Object> {
	private final ByteBuffer buffer;
	private final AsynchronousSocketChannel client;
	private final StringBuilder builder;
	private final long readTimeout;
	private final TimeUnit readTimeUnit;
	private final long writeTimeout;
	private final TimeUnit writeTimeUnit;
	
	public ReadCompletionHandler(ByteBuffer buffer,
			AsynchronousSocketChannel client, StringBuilder builder,
			long readTimeout, TimeUnit readTimeUnit, long writeTimeout,
			TimeUnit writeTimeUnit) {
		this.buffer = buffer;
		this.client = client;
		this.builder = builder;
		this.readTimeout = readTimeout;
		this.readTimeUnit = readTimeUnit;
		this.writeTimeout = writeTimeout;
		this.writeTimeUnit = writeTimeUnit;
	}
	
	@Override
	public void completed(Integer count, Object attachement) {
		System.out.println("Server: " + Thread.currentThread().getName()+ ": read " + count + " bytes from connection");		
		if (count == -1) {
			System.out.println("Server: " + Thread.currentThread().getName()+ ": connection closed on end-of-stream.");
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			this.buffer.flip();
			this.builder.append(StandardCharsets.US_ASCII.decode(buffer));
			this.buffer.clear();
			int position = this.builder.indexOf("exit");
			if(position != -1) {
				String message = builder.substring(0, position);
				ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes());
				this.client.write(writeBuffer, writeTimeout, writeTimeUnit, this.client, new WriteCompletionHandler());
			} else {
				// TODO хорошо бы передавать таймаут тоже извне
				this.client.read(this.buffer, readTimeout, readTimeUnit, null, this);				
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object readInfo) {
		if (exc instanceof AsynchronousCloseException) {
			System.out.println("Server: " + Thread.currentThread().getName()+ ": handler stopped due server socket shutdown");
		} if (exc instanceof InterruptedByTimeoutException) {
			System.out.println("Server: " + Thread.currentThread().getName()+ ": connection closed due read timeout.");
			try {
				this.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Server: " + Thread.currentThread().getName()+ ": failed reading from connection.");
			exc.printStackTrace();
		}
	}

}
