package resources;

import java.util.Random;

import outputs.Output;

public class RandomIntegerResource implements Resource {

	private final Random generator;
	
	public RandomIntegerResource(Random generator) {
		this.generator = generator;
	}

	@Override
	public Resource refine(String name, String value) {
		return this;
	}

	// We need to build textual representation of resource as an HTTP-response
	// HTTP-response has the spection format defined in RFCs. 
	// Should the resource know the format? NO! 
	// Why? There are many other representations/form, not only HTTP-response. FileOutput for example
	// We need someone who can convert Resource to text HTTP form
	// That's why we would use some Output, which builds final form of Resource
	@Override
	public void print(Output output) {
		output.print("Content-Type", "text/plain");
		String body = String.valueOf(generator.nextInt());
		output.print("Content-Length", String.valueOf(body.length()));
		output.print("X-Body", body);
	}

	@Override
	public boolean shouldCloseConnection() {
		return false;
	}
}
