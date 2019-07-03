package main;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	@Override
	public void completed(AsynchronousSocketChannel result,	AsynchronousServerSocketChannel server) {
		System.out.println("Server: " + Thread.currentThread().getName()+ " accepted connection.");
	}

	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel server) {
		System.out.println("Server: " + Thread.currentThread().getName()+ " failed accepting connection.");
		exc.printStackTrace();
	}
}



@Override
public void completed(AsynchronousSocketChannel result, Object attachment) {
    // we have access to field "server" and create another accept-request to OS in thread pool.
	// all the time we have one accepting thread in thread pool
	if (server.isOpen()){
        server.accept(null, this);
    }

    clientChannel = result;
    if ((clientChannel != null) && (clientChannel.isOpen())) {
        ReadWriteHandler handler = new ReadWriteHandler();
        ByteBuffer buffer = ByteBuffer.allocate(32);

        Map<String, Object> readInfo = new HashMap<>();
        readInfo.put("action", "read");
        readInfo.put("buffer", buffer);

        clientChannel.read(buffer, readInfo, handler);
     }
 }
 @Override
 public void failed(Throwable exc, Object attachment) {
     // process error
 }

});			
