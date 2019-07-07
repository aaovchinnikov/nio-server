package resources;

import outputs.Output;

public class PostResource implements Resource {

	@Override
	public Resource refine(String name, String value) {
		return null;
	}

	@Override
	public void print(Output output) {
	}

	@Override
	public boolean shouldCloseConnection() {
		return false;
	}

}
