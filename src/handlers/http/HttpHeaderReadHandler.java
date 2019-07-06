package handlers.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import outputs.StringBuilderOutput;
import resources.Resource;

public class HttpHeaderReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel client;
	private final StringBuilder builder;
	private final Resource resource;

	public HttpHeaderReadHandler(AsynchronousSocketChannel client,
			StringBuilder builder, Resource resource) {
		this.client = client;
		this.builder = builder;
		this.resource = resource;
	}

	private void closeSocket() {
		try {
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processHeader(String header) {
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
		Resource res = this.resource;
		for(Map.Entry<String, String> pair: pairs.entrySet()) {
			res = res.refine(pair.getKey(), pair.getValue());
		}
		final StringBuilder builder = new StringBuilder();
		res.print(new StringBuilderOutput(builder));
		this.client.write(ByteBuffer.wrap(builder.toString().getBytes()), null, new NoOpHandler());
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
