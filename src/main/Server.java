package main;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {
	private final SocketAddress socket;
	
	public Server(SocketAddress socket) {
		this.socket = socket;
	}
	
	// TODO Итак, внутренную обработку тредов для асинхронных операций выполняет AsynchronousChannelGroup
	// Исходя из javadoc-а с ней связан thread pool, который и исполняет операции.
	// Если канал при создании не указывает, с какой группой его ассоциировать, то он ассоциируется
	// с существующей в JVM "группой по умолчанию". Важно, что все потоки этой группы считаются как демоны,
	// т.е. их убьют, как только умрёт последний не-демон поток JVM.
	// Вывод - нужно создавать свою группу и работать через неё. Нужно читать примеры на AsynchronousChannelGroup
	
	// TODO Из javadoc-а на accept() не ясно, как будет вызвана операция - блокирующися/синхронным вызовом или нет.
	// Нужно поставить эксперимент.
	public void start() throws IOException {
		AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
		server.bind(socket);
		server.accept(attachment, handler);
		
		Future<AsynchronousSocketChannel> future = server.accept();
		// TODO handle TimeoutException at least
		try {
			AsynchronousSocketChannel client = future.get(10, TimeUnit.SECONDS);
//			while(true) {
				ByteBuffer buffer = ByteBuffer.allocate(this.size);
				Future<Integer> readResult = client.read(buffer);
				readResult.get(5, TimeUnit.SECONDS);
				buffer.flip();
				Future<Integer> writeResult = client.write(buffer);
				writeResult.get(5, TimeUnit.SECONDS);
				buffer.clear();
//			}
			client.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		server.close();	
	}
	
}
