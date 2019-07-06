package resources;

import outputs.Output;

/**
 * <p>Resource represents entity we'd like to communicate with by using HTTP-methods</p>
 * <p>Resource can print itself as HTTP-compliant response</p>
 * @author sansey
 *
 */
public interface Resource {
	/**
	 * Returns this-pointer if no special processing needed. Otherwise returns new refined instance of Resource
	 * @param name
	 * @param value
	 * @return
	 */
	Resource refine(String name, String value);
	void print(Output output);
}
