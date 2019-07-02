package main;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Semaphore;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Semaphore> {
	@Override
	public void completed(AsynchronousSocketChannel result,	Semaphore semaphore) {
		semaphore.release();
	}

	@Override
	public void failed(Throwable exc, Semaphore semaphore) {
		semaphore.release();
	}
}
