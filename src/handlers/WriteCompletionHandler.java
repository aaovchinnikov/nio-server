package handlers;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

	@Override
	public void completed(Integer count, AsynchronousSocketChannel client) {
		System.out.println("Server: " + Thread.currentThread().getName() 
				+ ": written " + count + " bytes to connection");
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, AsynchronousSocketChannel client) {
		System.out.println("Server: " + Thread.currentThread().getName()
				+ " failed writing to connection.");
		exc.printStackTrace();
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
