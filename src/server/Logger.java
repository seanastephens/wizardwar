package server;

public class Logger {

	public static final boolean LOGGING = true;

	public static void log(Object source, String message) {
		if (!LOGGING) {
			return;
		}
		String msg = String.format("%-15s", "[" + source.getClass().getSimpleName() + "]")
				+ message;
		System.out.println(msg);
	}

}
