package handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ReadCompletionHandler implements CompletionHandler<Integer, Object> {
	private final ByteBuffer buffer;
	private final AsynchronousSocketChannel client;
	private final StringBuilder builder;
	
	public ReadCompletionHandler(ByteBuffer buffer, AsynchronousSocketChannel client, StringBuilder builder) {
		this.buffer = buffer;
		this.client = client;
		this.builder = builder;
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
				this.client.write(writeBuffer, 0, TimeUnit.SECONDS, this.client, new WriteCompletionHandler());
			} else {
				// TODO хорошо бы передавать таймаут тоже извне
				this.client.read(this.buffer, 0, TimeUnit.SECONDS, null, this);				
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object readInfo) {
		System.out.println("Server: " + Thread.currentThread().getName()+ " failed reading from connection.");
		exc.printStackTrace();		
	}

}
