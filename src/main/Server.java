package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
	public void start(int port, int size) throws IOException {
		AsynchronousServerSocketChannel channel;
		try(ServerSocketChannel server = ServerSocketChannel.open()){
			server.bind(new InetSocketAddress(port));
			SocketChannel client = server.accept();
			System.out.println("Connection Set:  " + client.getRemoteAddress());
			ByteBuffer buffer = ByteBuffer.allocate(size);
			while (client.read(buffer) > 0) {
				System.out.println(buffer.asCharBuffer().toString());
				// another way with more control on encoding
				//String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
				buffer.clear();
			}
		} 
	}
	
	public void start(int port) throws IOException {
		this.start(port, 10_000);
	}
}
