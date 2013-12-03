package ex6;

@SuppressWarnings("serial")
public class Ex6MergeException extends Exception {

	public Ex6MergeException() {
		super("The files should have exactly one row in common.");
	}
}
