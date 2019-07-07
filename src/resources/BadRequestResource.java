package resources;

import outputs.Output;

public class BadRequestResource implements Resource {

	@Override
	public Resource refine(String name, String value) {
		return this;
	}

	@Override
	public void print(Output output) {
		output.print("X-Code", "400 Bad Request");
		output.print("Connection", "close");
	}

	@Override
	public boolean shouldCloseConnection() {
		return true;
	}
}
