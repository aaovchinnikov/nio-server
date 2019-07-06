package handlers.http;

import java.nio.channels.CompletionHandler;

public class NoOpHandler implements CompletionHandler<Integer, Object> {
	@Override
	public void completed(Integer count, Object attachement) {
	}

	@Override
	public void failed(Throwable exc, Object attachement) {
		exc.printStackTrace();
	}
}
