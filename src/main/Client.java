package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
	private final int port;
	public Client(int port) {
		this.port = port;
	}
	public void send(String message) throws IOException {
		try(SocketChannel socket = SocketChannel.open()){
			socket.connect(new InetSocketAddress(this.port));
			ByteBuffer buffer = ByteBuffer.allocate(message.length());
			buffer.put(message.getBytes());
			socket.write(buffer);
		}
	}
}
