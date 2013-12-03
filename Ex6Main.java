package ex6;

import java.io.FileInputStream;
import java.io.IOException;

public class Ex6Main {

	public static void main(String[] args) {
		
		/*
		 * EXTENSIONS
		 * ===================================================================
		 * More tolerant with input and output formats
		 * Returns column headings in alphabetical order (if option specified)
		 * Can specify columns to delete
		 * 
		 */

		// Check that we were given 2 filenames
		if (args.length < 2) {
			printUsageInstructions();
			return;
		}
		
		boolean alphabetic = false;
		String[] columnsToDelete = null;
		
		if(args.length > 2){
			int i = 0;
			if(args[2].equals("-a")){
				i++;
				alphabetic = true;
			}
			
			columnsToDelete = new String[args.length - 2];
			for(; i < columnsToDelete.length; i++){
				columnsToDelete[i] = args[i + 2];
			}
		}

		try {
			Ex6DataFile firstFile = new Ex6DataFile(
					new FileInputStream(args[0]), alphabetic, columnsToDelete);
			Ex6DataFile secondFile = new Ex6DataFile(new FileInputStream(
					args[1]), alphabetic, columnsToDelete);

			firstFile.merge(secondFile);

			firstFile.writeToStream(System.out);
		} catch (IOException e) {
			System.out.println("One of the files could not be read from.");
		} catch (Ex6ColumnHeadingsDataMismatchException e) {
			//System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Ex6MergeException e) {
			System.out.println("The two files could not be merged because" + 
			" they did not have exactly one column heading in common.");
		} catch (Ex6IllegalFormatException e) {
			System.out.println(e.getMessage());
		}

	}

	private static void printUsageInstructions() {
		System.out.println("USAGE:");
		System.out.println("Ex6Main filename1 filename2 options columntodelete1 columntodelete2 columntodeleten");
		System.out.println("Options:");
		System.out.println("-a Alphabetic order");
	}

}
