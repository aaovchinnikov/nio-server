package outputs;

/**
 * Output based on {@link StringBuilder} for building final HTTP-response.
 * The order of invocation with different arguments is important, cause it defines
 * the order of strings in response.
 * @author sansey
 *
 */
public class StringBuilderOutput implements Output {
	private final StringBuilder builder;
	public StringBuilderOutput(StringBuilder builder) {
		this.builder = builder;
	}
	@Override
	public void print(String name, String value) {
		if (this.builder.length() == 0) {
			this.builder.append("HTTP/1.1").append(" ");
			if(name.equals("X-Code")) {
				this.builder.append(value);
				this.builder.append("\r\n");
				return;
			} else {
				this.builder.append("200 OK\r\n");
			}
		}
		if (name.equals("X-Body")) {
			this.builder.append("\r\n").append(value);
		} else {
			this.builder.append(name).append(": ").append(value).append("\r\n");
		}
	}
}
