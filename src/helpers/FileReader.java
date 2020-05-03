package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReader {

	private final String rootFolder;
	private final Pattern inputPattern;
	private final Pattern EMPTY_CSV_PATTERN = Pattern.compile( "^[,\\W]+$" );

	public FileReader( String rootFolder, Pattern inputPattern ) {
		this.rootFolder = rootFolder;
		this.inputPattern = inputPattern;
	}

	/**
	 * Extracts the filenames of all files in a given folder to a set of Strings
	 *
	 * @return the filenames with the root folder stripped out
	 */
	public Set<String> getFileNames() {
		try ( Stream<Path> walk = Files.walk( Paths.get( rootFolder ) ) ) {
			return walk.filter(Files::isRegularFile)
					.map( Path::toString )
					.filter( inputPattern.asPredicate() )
					.map( s -> s.replaceAll( "^" + rootFolder, "" ) )
					.collect( Collectors.toSet());
		} catch ( IOException e ) {
			e.printStackTrace();
			return new HashSet<>();
		}
	}

	/**
	 * Reads in a given file and returns a List of lines from the file
	 *
	 * No analysis of the file format is performed
	 *
 	 * @param filename
	 * 		the file to analyse
	 * @return a list of lines
	 */
	public List<String> readFileLines( String filename ) {
		List<String> rows = new ArrayList<>();
		try {
			File file = new File( rootFolder + filename );
			InputStream inputStream = new FileInputStream( file );
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
			rows = bufferedReader.lines()
					.map( line -> line.replaceAll("\uFEFF", "" ) ) // BOM removal
					.filter( line -> !line.isEmpty() )
					.filter( line -> !EMPTY_CSV_PATTERN.matcher(line).matches() )
					.collect( Collectors.toList() );
			bufferedReader.close();
		} catch ( IOException e) {
			System.out.println( String.format( "Error reading file %s: %s", filename, e ) );
		}
		return rows;
	}
}
