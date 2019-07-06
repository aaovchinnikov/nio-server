package handlers.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
		Map<String,String> pairs = new HashMap<String, String>();
		String[] lines = header.split("\r\n");
		String[] parts = lines[0].split(" ");
		pairs.put("X-Method", parts[0]);
		pairs.put("X-Query", parts[1]);
		pairs.put("X-Protocol", parts[2]);
		for(int idx = 1; idx < lines.length; idx++) {
			parts = lines[idx].split(": ");
			pairs.put(parts[0].trim(),parts[1].trim());
		}


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
