package main;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	//TODO need separate buffers for every read-handler instance

	@Override
	public void completed(AsynchronousSocketChannel client,	AsynchronousServerSocketChannel server) {
		System.out.println("Server: " + Thread.currentThread().getName()+ " accepted connection.");
		server.accept(server, this);
		// I like this call, but should test timeouts
		// read until "exit" sustring is reached in input. Then return the input to client
		client.read(buffer, 0, TimeUnit.SECONDS, attachment, handler);
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




class ReadWriteHandler implements
CompletionHandler<Integer, Map<String, Object>> {
   
  @Override
  public void completed(
    Integer result, Map<String, Object> attachment) {
      Map<String, Object> actionInfo = attachment;
      String action = (String) actionInfo.get("action");

      if ("read".equals(action)) {
          ByteBuffer buffer = (ByteBuffer) actionInfo.get("buffer");
          buffer.flip();
          actionInfo.put("action", "write");

          clientChannel.write(buffer, actionInfo, this);
          buffer.clear();

      } else if ("write".equals(action)) {
          ByteBuffer buffer = ByteBuffer.allocate(32);

          actionInfo.put("action", "read");
          actionInfo.put("buffer", buffer);

          clientChannel.read(buffer, actionInfo, this);
      }
  }
   
  @Override
  public void failed(Throwable exc, Map<String, Object> attachment) {
      // 
  }
}
