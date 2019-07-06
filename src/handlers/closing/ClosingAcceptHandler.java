package handlers.closing;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ClosingAcceptHandler implements CompletionHandler<AsynchronousSocketChannel,
	AsynchronousServerSocketChannel> {

	@Override
	public void completed(AsynchronousSocketChannel client,
			AsynchronousServerSocketChannel server) {
		try {
			client.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void failed(Throwable exc,
			AsynchronousServerSocketChannel attachment) {
		
	}

}
