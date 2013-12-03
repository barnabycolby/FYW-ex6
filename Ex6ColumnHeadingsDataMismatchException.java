package ex6;

@SuppressWarnings("serial")
public class Ex6ColumnHeadingsDataMismatchException extends Exception {

	public Ex6ColumnHeadingsDataMismatchException() {
		super(
				"Number of column headings mismatched the number of elements in the data.");
	}

	public Ex6ColumnHeadingsDataMismatchException(String message) {
		super(message);
	}
}
