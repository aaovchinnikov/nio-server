package resources;

import outputs.Output;

public final class HelloWorldResource implements Resource {
	private final static String BODY = "Hello World!";

	@Override
	public Resource refine(String name, String value) {
		// We don't need to return anyone else. That's our duty to finally handle request 
		return this;
	}

	@Override
	public void print(Output output) {
		// Here we use static configuring of resource
		// providing the output the necessary info.
		output.print("Content-Type", "text/plain");
		output.print("Content-Length", String.valueOf(BODY.length()));
		output.print("X-Body", BODY);
	}
}
