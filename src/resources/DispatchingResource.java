package resources;

import java.util.Random;

import outputs.Output;

public class DispatchingResource implements Resource {
	private final static String NOT_FOUND = "Not found!";
	
	@Override
	public Resource refine(String name, String value) {
		if (name.equals("X-Query")) {
			if (value.equals("/")) {
				return new HelloWorldResource();
			} else if (value.equals("/random")) {
				return new RandomIntegerResource(new Random());
			}
		}
		return this;
	}

	@Override
	public void print(Output output) {
		output.print("X-Code", "404 Not Found");
		output.print("Connection", "close");
		output.print("Content-Type", "text/plain");
		output.print("Content-Length", String.valueOf(NOT_FOUND.length()));
		output.print("X-Body", NOT_FOUND);
	}

	@Override
	public boolean shouldCloseConnection() {
		return true;
	}
}
