package outputs;

public interface Output {
	/**
	 * {@link Output#print(String, String)} invocation order may be important in some implementations
	 */
	void print(String name, String value);
}
