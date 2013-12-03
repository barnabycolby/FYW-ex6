package ex6;

@SuppressWarnings("serial")
public class Ex6IllegalFormatException extends Exception {
	public Ex6IllegalFormatException(String message) {
		super(message);
	}

	public Ex6IllegalFormatException() {
		super(
				"Odd number of quotes. A single quote should be represented by a double quote instead.");
	}
}
