package ex6;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Ex6DataLine {

	private LinkedHashMap<String, String> data;
	private boolean alphabetic;

	/**
	 * The constructor
	 * @param columnHeadings
	 *            The comma separated string of column headings.
	 * @param dataLine
	 *            The comma separated string of data values.
	 * @param columnsToDelete
	 *            The columns to delete from the output (if any were specified).
	 * @throws Ex6ColumnHeadingsDataMismatchException
	 *             Thrown if the number of column headings and data values do
	 *             not match.
	 * @throws Ex6IllegalFormatException
	 *             Thrown if an IO error occurs.
	 */
	public Ex6DataLine(String columnHeadings, String dataLine,
			String[] columnsToDelete)
			throws Ex6ColumnHeadingsDataMismatchException,
			Ex6IllegalFormatException {
		// Should check whether the number of headings and data values are the
		// same
		if (countEntries(columnHeadings) != countEntries(dataLine)) {
			throw new Ex6ColumnHeadingsDataMismatchException(
					"The number of columns in the line \"" + dataLine
							+ "\" was inconsistent.");
		}

		String[] columnHeadingsArray = parseEntries(columnHeadings);
		String[] dataValuesArray = parseEntries(dataLine);

		// Store data
		for (int i = 0; i < columnHeadingsArray.length; i++) {
			// Check that column is not one of the columnsToDelete strings
			boolean delete = false;
			if (columnsToDelete != null) {
				for (int j = 0; j < columnsToDelete.length; j++) {
					if (columnHeadingsArray[i].equals(columnsToDelete[j])) {
						delete = true;
					}
				}
			}

			if (!delete) {
				this.setDataValue(columnHeadingsArray[i], dataValuesArray[i]);
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	private int countEntries(String entries) throws Ex6IllegalFormatException {
		int numberOfEntries = 0;

		int commaCount = this.countOccurrences(",", entries);
		int quotesCount = this.countOccurrences("\"", entries);
		int doubleQuotesCount = this.countOccurrences("\"\"", entries);

		if (commaCount == 0) {
			return 1;
		}

		if (quotesCount - (2 * doubleQuotesCount) == 0) {
			return commaCount + 1;
		}

		// Must have an even number of quotes
		if ((quotesCount % 2) != 0) {
			throw new Ex6IllegalFormatException();
		}

		// Change any double quotes to a different character to make my life
		// easier
		entries = doubleQuotesToAsterisks(entries);

		String subString = entries;
		while (subString.contains(",") || subString.contains("\"")) {
			Ex6SymbolsEnum symbolCase = nextSymbol(subString);

			switch (symbolCase) {
			case COMMA:
				numberOfEntries++;
				subString = subString.substring(subString.indexOf(",") + 1);
				break;
			case QUOTE:
				// Find next quote symbol and skip past it
				subString = subString.substring(subString.indexOf("\"") + 1);
				subString = subString.substring(subString.indexOf("\"") + 1);
				break;
			}
		}

		// Add 1 because we still have one entry even when there are no commas
		return numberOfEntries + 1;
	}

	private Ex6SymbolsEnum nextSymbol(String subString) {
		int indexOfComma = subString.indexOf(",");
		int indexOfQuote = subString.indexOf("\"");
		int indexOfDoubleQuote = subString.indexOf("\"\"");

		if (indexOfComma == -1 && indexOfQuote == -1
				&& indexOfDoubleQuote == -1) {
			return Ex6SymbolsEnum.NONE;
		}

		return smallestPositiveSymbol(indexOfComma, indexOfQuote,
				indexOfDoubleQuote);
	}

	private String doubleQuotesToAsterisks(String entries) {
		while (entries.contains("\"\"")) {
			int indexOfDoubleQuote = entries.indexOf("\"\"");
			String prefix = entries.substring(0, indexOfDoubleQuote);
			String suffix = entries.substring(indexOfDoubleQuote + 2);

			entries = prefix + "*" + suffix;
		}
		return entries;
	}

	/**
	 * Sets the data value of the specified column heading.
	 * @param columnHeading The column heading to change the data value of.
	 * @param dataValue The new data value.
	 */
	public void setDataValue(String columnHeading, String dataValue) {
		this.getData().put(columnHeading, dataValue);
	}

	/**
	 * Returns the data value of the specified column heading.
	 * @param columnHeading The column heading to retrieve the value for.
	 * @return Returns the data value of the specified column heading.
	 */
	public String getDataValue(String columnHeading) {
		if (this.getData().containsKey(columnHeading)) {
			return this.getData().get(columnHeading);
		}

		return "";
	}

	@SuppressWarnings("incomplete-switch")
	private String[] parseEntries(String entries)
			throws Ex6IllegalFormatException {
		String[] returnArray = new String[countEntries(entries)];
		String subString = entries;
		int i = 0;
		String holdingString = "";

		while (subString.contains(",") || subString.contains("\"")) {
			switch (nextSymbol(subString)) {
			case COMMA:
				returnArray[i] = holdingString.concat(subString.substring(0,
						subString.indexOf(",")));
				holdingString = "";
				i++;
				subString = subString.substring(subString.indexOf(",") + 1);
				break;
			case QUOTE:
				int indexOfSecondQuote = (subString.substring(1)).indexOf("\"") + 1;
				if (indexOfSecondQuote == -1) {
					throw new Ex6IllegalFormatException();
				}
				holdingString = holdingString.concat(subString.substring(0,
						indexOfSecondQuote + 1));
				subString = subString.substring(indexOfSecondQuote + 1);
				break;
			case DOUBLEQUOTE:
				int indexOfDoubleQuote = subString.indexOf("\"\"");
				holdingString = holdingString.concat(subString.substring(0,
						indexOfDoubleQuote + 2));
				subString = subString.substring(indexOfDoubleQuote + 2);
				break;
			}
		}

		// Add the final entry to the string array
		returnArray[i] = holdingString.concat(subString);

		return returnArray;
	}

	@SuppressWarnings("incomplete-switch")
	private Ex6SymbolsEnum smallestPositiveSymbol(int indexOfComma,
			int indexOfQuote, int indexOfDoubleQuote) {

		Ex6SymbolsEnum[] sortedArray = new Ex6SymbolsEnum[3];

		if (indexOfComma < indexOfQuote && indexOfComma < indexOfDoubleQuote) {
			sortedArray[0] = Ex6SymbolsEnum.COMMA;

			if (indexOfQuote < indexOfDoubleQuote) {
				sortedArray[1] = Ex6SymbolsEnum.QUOTE;
				sortedArray[2] = Ex6SymbolsEnum.DOUBLEQUOTE;
			} else {
				sortedArray[1] = Ex6SymbolsEnum.DOUBLEQUOTE;
				sortedArray[2] = Ex6SymbolsEnum.QUOTE;
			}
		}

		else if (indexOfQuote < indexOfComma
				&& indexOfQuote < indexOfDoubleQuote) {
			sortedArray[0] = Ex6SymbolsEnum.QUOTE;

			if (indexOfComma < indexOfDoubleQuote) {
				sortedArray[1] = Ex6SymbolsEnum.COMMA;
				sortedArray[2] = Ex6SymbolsEnum.DOUBLEQUOTE;
			} else {
				sortedArray[1] = Ex6SymbolsEnum.DOUBLEQUOTE;
				sortedArray[2] = Ex6SymbolsEnum.COMMA;
			}
		}

		else {
			sortedArray[0] = Ex6SymbolsEnum.DOUBLEQUOTE;

			if (indexOfQuote < indexOfComma) {
				sortedArray[1] = Ex6SymbolsEnum.QUOTE;
				sortedArray[2] = Ex6SymbolsEnum.COMMA;
			} else {
				sortedArray[1] = Ex6SymbolsEnum.COMMA;
				sortedArray[2] = Ex6SymbolsEnum.QUOTE;
			}
		}

		// Return the first >= 0 value in the array

		for (int i = 0; i < sortedArray.length; i++) {
			int value = -1;

			switch (sortedArray[i]) {
			case COMMA:
				value = indexOfComma;
				break;
			case QUOTE:
				value = indexOfQuote;
				break;
			case DOUBLEQUOTE:
				value = indexOfDoubleQuote;
				break;
			}

			if (value >= 0) {
				return sortedArray[i];
			}
		}

		return Ex6SymbolsEnum.NONE;
	}

	private int countOccurrences(String searchTerm, String string) {
		String subString = string;
		int occurrences = 0;

		while (subString.contains(searchTerm)) {
			occurrences++;

			subString = subString.substring(subString.indexOf(searchTerm)
					+ searchTerm.length());
		}

		return occurrences;
	}

	public void setData(LinkedHashMap<String, String> data) {
		this.data = data;
	}

	public LinkedHashMap<String, String> getData() {
		if (this.data == null) {
			this.data = new LinkedHashMap<String, String>();
		}

		return this.data;
	}

	public String[] getHeadings() {
		if (this.getData().size() == 0) {
			return null;
		}

		String[] returnArray = new String[this.getData().size()];

		Iterator<Entry<String, String>> it = this.getData().entrySet()
				.iterator();

		for (int i = 0; it.hasNext(); i++) {
			Entry<String, String> entry = it.next();
			returnArray[i] = (String) entry.getKey();
		}

		if (this.alphabetic) {
			// Sort the array alphabetically
			for (int i = 0; i < returnArray.length; i++) {
				int alphabeticallyFirst = i;

				for (int j = i; j < returnArray.length; j++) {
					if (returnArray[j]
							.compareTo(returnArray[alphabeticallyFirst]) < 0) {
						returnArray = this.swapArrayEntries(returnArray, j,
								alphabeticallyFirst);
					}
				}
			}
		}

		return returnArray;
	}

	private String[] swapArrayEntries(String[] array, int a, int b) {
		String tempValue = array[a];
		array[a] = array[b];
		array[b] = tempValue;
		return array;
	}

	public void setAlphabetic(boolean alphabetic) {
		this.alphabetic = alphabetic;
	}
}
