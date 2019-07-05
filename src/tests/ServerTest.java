package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import main.Server;

class ServerTest {

	private Thread createAndStartServer(SocketAddress socket, int size) throws InterruptedException {
		return createAndStartServer(socket, size, 0, TimeUnit.SECONDS, 0, TimeUnit.SECONDS);
	}
	
	private Thread createAndStartServer(SocketAddress socket, int size, long readTimeout,
			TimeUnit readTimeUnit, long writeTimeout, TimeUnit writeTimeUnit) throws InterruptedException {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Server server = new Server(socket, size, readTimeout, readTimeUnit, writeTimeout, writeTimeUnit);
				try {
					server.start();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		});
		thread.start();
		Thread.sleep(500); // time for socket allocation
		return thread;
	}
	
	@Test
	synchronized void serverStops() throws InterruptedException {
		System.out.println("\n---serverStops test invoked---");
		final int port = 8080;
		final SocketAddress socket = new InetSocketAddress(port);
		Thread thread = createAndStartServer(socket, 1_000);
		thread.interrupt();
		thread.join();
	}
	
	@Test
	synchronized void tcpConnect() throws IOException, InterruptedException {
		System.out.println("\n---tcpConnect test invoked---");
		final int port = 8080;
		final SocketAddress socket = new InetSocketAddress(port);
		final Thread thread = createAndStartServer(socket, 1_000);
		SocketChannel client = SocketChannel.open(socket);
		Thread.sleep(500); // time for socket allocation
		client.close();
		thread.interrupt();
		thread.join();
	}

	@Test
	synchronized void sendMessage() throws IOException, InterruptedException {
		System.out.println("\n---sendMessage test invoked---");
		final int port = 8080;
		final SocketAddress socket = new InetSocketAddress(port);
		final Thread thread = createAndStartServer(socket, 1_000);
		final String message = "Hello World!";
		final String EXIT = "exit";
		SocketChannel client = SocketChannel.open(socket);
		Thread.sleep(500); // time for socket allocation
		ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
		client.write(buffer);
		buffer = ByteBuffer.wrap(EXIT.getBytes());
		client.write(buffer);
		buffer = ByteBuffer.allocate(100);
		client.read(buffer);
		buffer.flip();
		client.close();
		thread.interrupt();
		thread.join();
		assertEquals(message, StandardCharsets.US_ASCII.decode(buffer).toString());
	}

	@Test
	synchronized void readTimeout() throws IOException, InterruptedException {
		System.out.println("\n---readTimeout test invoked---");
		final int port = 8080;
		final SocketAddress socket = new InetSocketAddress(port);
		final Thread thread = createAndStartServer(socket, 1_000, 500, TimeUnit.MILLISECONDS, 0, TimeUnit.SECONDS);
		SocketChannel client = SocketChannel.open(socket);
		Thread.sleep(500); // time for socket allocation
		Thread.sleep(3_000);
		int count = client.write(ByteBuffer.wrap("Hello World!exit".getBytes()));
		System.out.println("Test: bytes written to socket: " + count);
		count = client.read(ByteBuffer.allocate(1000));
		System.out.println("Test: bytes read from socket: " + count);
		assertEquals(-1, count);
		thread.interrupt();
		thread.join();
	}
}
