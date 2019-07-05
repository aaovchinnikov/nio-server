package handlers;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;

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
		if (exc instanceof AsynchronousCloseException) {
			System.out.println("Server: " + Thread.currentThread().getName()+ ": write handler stopped due server socket shutdown");
		} else if (exc instanceof InterruptedByTimeoutException){
			System.out.println("Server: " + Thread.currentThread().getName()+ ": connection closed due write timeout.");
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Server: " + Thread.currentThread().getName()
					+ ": failed writing to connection.");
			exc.printStackTrace();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
