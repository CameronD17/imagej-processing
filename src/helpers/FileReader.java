package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileReader {

	/**
	 * Reads in a given file and returns a List of lines from the file
	 *
	 * No analysis of the file format is performed
	 *
 	 * @param filename
	 * 		the file to analyse
	 * @return a list of lines
	 */
	public static List<String> readFileLines( String filename ) {
		List<String> rows = new ArrayList<>();
		try {
			File file = new File(filename);
			InputStream inputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			rows = bufferedReader.lines()
					.map( line -> line.replaceAll("\uFEFF", "") ) // BOM removal
					.collect( Collectors.toList());
			bufferedReader.close();
		} catch ( IOException e) {
			System.out.println( String.format( "Error reading file %s: %s", filename, e ) );
		}
		return rows;
	}
}
