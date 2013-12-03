package ex6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Ex6DataFile {

	// Stores input stream
	private final BufferedReader is;

	// Stores data lines
	private ArrayList<Ex6DataLine> dataLines;

	/**
	 * Constructor
	 * @param is The input stream to retrieve data from.
	 * @param alphabetic Specifies whether the column headings should be in alphabetic order when writeToStream is called.
	 * @param columnsToDelete The columns to delete from the output if any were specified.
	 * @throws IOException Thrown if an IO error occurs.
	 * @throws Ex6ColumnHeadingsDataMismatchException Thrown if the number of column headings and data values in a line are different.
	 * @throws Ex6IllegalFormatException Thrown if the format of a line is not recognized.
	 */
	public Ex6DataFile(InputStream is, boolean alphabetic, String[] columnsToDelete) throws IOException,
			Ex6ColumnHeadingsDataMismatchException, Ex6IllegalFormatException {
		// Convert input stream to buffered reader so that we can read lines
		this.is = new BufferedReader(new InputStreamReader(is));

		// For each line in the input stream create an Ex6DataLine object
		String line;

		// First line should be the column headings
		if ((line = this.getInputStream().readLine()) == null) {
			return;
		}

		String columnHeadings = line;

		// Must have at least one line of data
		if ((line = this.getInputStream().readLine()) == null) {
			return;
		}

		String dataLine;

		do {
			dataLine = line;

			Ex6DataLine tempDataLine = new Ex6DataLine(columnHeadings, dataLine, columnsToDelete);
			tempDataLine.setAlphabetic(alphabetic);
			this.getDataLines().add(tempDataLine);
		} while ((line = this.getInputStream().readLine()) != null);

	}

	/**
	 * Writes the data in the file to the output stream.
	 * @param os The output stream to write to.
	 * @throws IOException Thrown if an IO error occurs.
	 */
	public void writeToStream(OutputStream os) throws IOException {
		// First we must check that this Ex6DataFile is not empty
		if (this.getDataLines().isEmpty()) {
			return;
		}

		int i, j;

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

		String[] columnHeadingsArray = this.getDataLines().get(0).getHeadings();

		// Output column headings first
		if (this.getDataLines().size() > 0) {
			String columnHeadings = stringArrayToCommaString(columnHeadingsArray);

			bw.write(columnHeadings);
			bw.newLine();
		} else {
			return;
		}

		// To store the data values
		String[] dataRowArray = new String[columnHeadingsArray.length];

		for (i = 0; i < this.getDataLines().size(); i++) {
			for (j = 0; j < columnHeadingsArray.length; j++) {
				dataRowArray[j] = this.getDataLines().get(i)
						.getDataValue(columnHeadingsArray[j]);
			}

			String dataRow = stringArrayToCommaString(dataRowArray);

			bw.write(dataRow);

			if (i < this.getDataLines().size() - 1) {
				bw.newLine();
			}
		}

		bw.flush();
	}

	/**
	 * Merges two Ex6Datafiles together if they have exactly one common column heading.
	 * @param dataFile The Ex6DataFile to merge with this one.
	 * @throws Ex6MergeException Thrown if the two Ex6DataFiles do not have exactly one column heading in common.
	 * @throws Ex6ColumnHeadingsDataMismatchException Thrown if the number of column headings and data values in a line are different.
	 * @throws Ex6IllegalFormatException Thrown if the format of a line is not recognized.
	 */
	public void merge(Ex6DataFile dataFile) throws Ex6MergeException,
			Ex6ColumnHeadingsDataMismatchException, Ex6IllegalFormatException {

		// First check whether one or both of the Ex6Files are empty
		if (this.getDataLines().isEmpty() || dataFile.getDataLines().isEmpty()) {
			return;
		}

		String[] firstHeadings = this.getDataLines().get(0).getHeadings();
		String[] secondHeadings = dataFile.getDataLines().get(0).getHeadings();
		Ex6DataLine firstFileFirstLine = this.getDataLines().get(0);
		Ex6DataLine secondFileFirstLine = dataFile.getDataLines().get(0);

		// Check how many columns the files have in common
		String commonHeading = commonHeading(firstHeadings, secondHeadings);

		// Produce the new set of headings
		String[] newHeadings = mergeHeadings(firstFileFirstLine,
				secondFileFirstLine, commonHeading);

		ArrayList<Ex6DataLine> newDataLines = new ArrayList<Ex6DataLine>();
		String[] tempDataArray = new String[newHeadings.length];

		for (int i = 0; i < this.getDataLines().size(); i++) {

			// Treat all lines as if they don't need any extra information added
			// to them
			for (int j = 0; j < newHeadings.length; j++) {
				tempDataArray[j] = this.getDataLines().get(i)
						.getDataValue(newHeadings[j]);
			}

			// Check whether the value of the common heading appears in the
			// second file
			for (int k = 0; k < dataFile.getDataLines().size(); k++) {
				if (this.getDataLines()
						.get(i)
						.getDataValue(commonHeading)
						.equals(dataFile.getDataLines().get(k)
								.getDataValue(commonHeading))) {

					// We found a common value, merge the two lines
					for (int l = 0; l < newHeadings.length; l++) {
						String dataValue = dataFile.getDataLines().get(k)
								.getDataValue(newHeadings[l]);

						if (!dataValue.equals("")) {
							tempDataArray[l] = dataValue;
						}
					}

					break;
				}
			}

			newDataLines.add(new Ex6DataLine(
					stringArrayToCommaString(newHeadings),
					stringArrayToCommaString(tempDataArray), null));
		}

		// Store the new list of data lines
		this.setDataLines(newDataLines);
	}

	private String[] mergeHeadings(Ex6DataLine firstFileFirstLine,
			Ex6DataLine secondFileFirstLine, String commonHeading) {
		int i;
		int firstFileHeadingsLength = firstFileFirstLine.getHeadings().length;
		int secondFileHeadingsLength = secondFileFirstLine.getHeadings().length;
		int numberOfHeadings = firstFileHeadingsLength
				+ secondFileHeadingsLength - 1;
		String[] newHeadings = new String[numberOfHeadings];
		for (i = 0; i < firstFileHeadingsLength; i++) {
			newHeadings[i] = firstFileFirstLine.getHeadings()[i];
		}

		// Merge second files headings
		for (int j = 0; j < secondFileHeadingsLength; j++) {
			if (!secondFileFirstLine.getHeadings()[j].equals(commonHeading)) {
				newHeadings[i] = secondFileFirstLine.getHeadings()[j];
				i++;
			}
		}

		return newHeadings;
	}

	private String commonHeading(String[] firstHeadings, String[] secondHeadings)
			throws Ex6MergeException {
		int numberOfCommonHeadings = 0;
		String commonHeading = null;

		for (int i = 0; i < firstHeadings.length; i++) {
			for (int j = 0; j < secondHeadings.length; j++) {
				if (firstHeadings[i].equals(secondHeadings[j])) {
					// We found a common column between the files
					commonHeading = firstHeadings[i];
					numberOfCommonHeadings++;
				}
			}
		}

		if (numberOfCommonHeadings != 1) {
			throw new Ex6MergeException();
		}

		return commonHeading;
	}

	private String stringArrayToCommaString(String[] stringArray) {
		String returnString = "";

		for (int i = 0; i < stringArray.length; i++) {
			returnString = returnString.concat(stringArray[i]);

			// Add a comma if the element is not the last
			if (i < stringArray.length - 1) {
				returnString = returnString.concat(",");
			}
		}

		return returnString;
	}

	public BufferedReader getInputStream() {
		return is;
	}

	public ArrayList<Ex6DataLine> getDataLines() {
		if (this.dataLines == null) {
			this.dataLines = new ArrayList<Ex6DataLine>();
		}

		return this.dataLines;
	}

	public void setDataLines(ArrayList<Ex6DataLine> dataLines) {
		this.dataLines = dataLines;
	}

}
