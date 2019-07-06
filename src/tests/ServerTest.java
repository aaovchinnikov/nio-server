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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handlers.AcceptCompletionHandler;
import main.Server;

class ServerTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Thread createAndStartServer(SocketAddress socket, int size) throws InterruptedException {
		return createAndStartServer(socket, size, 0, TimeUnit.SECONDS, 0, TimeUnit.SECONDS);
	}
	
	private Thread createAndStartServer(SocketAddress socket, int size, long readTimeout,
			TimeUnit readTimeUnit, long writeTimeout, TimeUnit writeTimeUnit) throws InterruptedException {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Server server = new Server(socket, logger, new AcceptCompletionHandler(size, readTimeout, readTimeUnit, writeTimeout, writeTimeUnit, logger));
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
		this.logger.info("---serverStops test invoked---");
		final int port = 8080;
		final SocketAddress socket = new InetSocketAddress(port);
		Thread thread = createAndStartServer(socket, 1_000);
		thread.interrupt();
		thread.join();
	}
	
	@Test
	synchronized void tcpConnect() throws IOException, InterruptedException {
		this.logger.info("---tcpConnect test invoked---");
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
		this.logger.info("---sendMessage test invoked---");
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
		this.logger.info("---readTimeout test invoked---");
		final int port = 8080;
		final SocketAddress socket = new InetSocketAddress(port);
		final Thread thread = createAndStartServer(socket, 1_000, 500, TimeUnit.MILLISECONDS, 0, TimeUnit.SECONDS);
		SocketChannel client = SocketChannel.open(socket);
		Thread.sleep(500); // time for socket allocation
		Thread.sleep(3_000);
		int count = client.write(ByteBuffer.wrap("Hello World!exit".getBytes()));
		this.logger.info("Test: bytes written to socket: {}", count);
		count = client.read(ByteBuffer.allocate(1000));
		this.logger.info("Test: bytes read from socket: {}", count); 
		assertEquals(-1, count);
		thread.interrupt();
		thread.join();
	}
}
