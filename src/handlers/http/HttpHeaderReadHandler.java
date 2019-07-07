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

/**
 * 
 * @author sansey
 *
 */
public class HttpHeaderReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel client;
	private final StringBuilder builder;
	private final Resource resource;
	/** {@link Operation} to handle */
	private Operation operation;
	private ByteBuffer readBuffer = null;
	
	public HttpHeaderReadHandler(AsynchronousSocketChannel client,
			StringBuilder builder, Resource resource, Operation operation) {
		this.client = client;
		this.builder = builder;
		this.resource = resource;
		this.operation = operation;
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
		ByteBuffer writeBuffer = ByteBuffer.wrap(builder.toString().getBytes());
		this.operation = Operation.WRITE;
		this.client.write(writeBuffer, writeBuffer, this);
	}
	
	private void handleRead(Integer count, ByteBuffer buffer) {
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
	
	private void handleWrite(Integer count, ByteBuffer buffer) {
		if(buffer.hasRemaining()) {
			buffer.compact();
			buffer.flip();
			this.client.write(buffer, buffer, this);
		} else {
			this.operation = Operation.READ;
			this.client.read(this.readBuffer, this.readBuffer, this); 
		}
	}
	
	@Override
	public void completed(Integer count, ByteBuffer buffer) {
		if (this.operation.isRead()) {
			if (this.readBuffer == null) {
				this.readBuffer = buffer;
			}
			handleRead(count, buffer);
		} else if(this.operation.isWrite()) {
			handleWrite(count, buffer);
		} else {
			throw new IllegalStateException("Operation is not READ or WRITE");
		}
	}

	
	@Override
	public void failed(Throwable exc, ByteBuffer buffer) {
		exc.printStackTrace();
	}

}
