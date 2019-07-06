package handlers.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class HttpHeaderReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel client;
	private final StringBuilder builder;

	public HttpHeaderReadHandler(AsynchronousSocketChannel client,
			StringBuilder builder) {
		this.client = client;
		this.builder = builder;
	}

	private void closeSocket() {
		try {
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processHeader(String header) {
		//TODO parse and process HTTP header
		
	}
	
	@Override
	public void completed(Integer count, ByteBuffer buffer) {
		if (count == -1) {
			closeSocket();
		} else {
			buffer.flip();
			this.builder.append(StandardCharsets.US_ASCII.decode(buffer));
			buffer.clear();
			int position = this.builder.indexOf("\r\n\r\n");
			final String header = this.builder.substring(0, position);
			this.builder.delete(0, position + 2);
			if(position != -1) {
				processHeader(header);
			} else {
				this.client.read(buffer, buffer, this);
			}
		}	
	}

	
	@Override
	public void failed(Throwable exc, ByteBuffer buffer) {
		exc.printStackTrace();
	}

}
