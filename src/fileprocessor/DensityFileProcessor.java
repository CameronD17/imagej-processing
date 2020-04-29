package fileprocessor;

import static processors.DensityProcessor.PARTICLES_SUFFIX;
import static processors.DensityProcessor.ROI_SUFFIX;
import static processors.DensityProcessor.ROOT_FOLDER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import helpers.FileReader;

public class DensityFileProcessor {

	private static final Pattern CSV_PATTERN = Pattern.compile(".*\\.csv");

	public static List<String> getAllFileNames() {
		try ( Stream<Path> walk = Files.walk( Paths.get( ROOT_FOLDER ) ) ) {
			return walk.filter(Files::isRegularFile)
					.map( Path::toString )
					.filter( CSV_PATTERN.asPredicate() )
					.collect( Collectors.toList());
		} catch ( IOException e ) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static Set<String> getBareFileNames() {
		return getAllFileNames().stream()
				.map( s -> s.replaceAll( "^" + ROOT_FOLDER, "" )
						.replaceAll( PARTICLES_SUFFIX + "$", "" )
						.replaceAll( ROI_SUFFIX + "$", "" ) )
				.collect( Collectors.toSet() );
	}

	/**
	 * Reads in a given file, parsing to a list of particle counts by:
	 * - Skipping the first line of the file (header row)
	 * - Splitting subsequent lines by commas and extracting the 2nd value (Count column)
	 *
	 * No analysis of the file format is performed
	 *
	 * @param filename
	 * 		the particles file to analyse
	 * @return a list of particle counts
	 */
	public static List<Long> processParticlesFile( String filename ) {
		List<String> fileLines = FileReader.readFileLines( filename );

		return fileLines.stream().skip(1) // Skip header row
				.map( (line) -> Long.valueOf( line.split( "," )[1] ) ) // Extract 2nd column value
				.collect( Collectors.toList() );
	}

	/**
	 * Reads in a given file, parsing to a list of areas by:
	 * - Skipping the first line of the file (header row)
	 * - Splitting the 2nd line by commas, and taking every 8th value (Area column)
	 *
	 * No analysis of the file format is performed
	 *
	 * @param filename
	 * 		the ROI file to analyse
	 * @return a list of areas
	 */
	public static List<Double> processROIFile( String filename ) {
		List<String> fileLines = FileReader.readFileLines( filename );

		Optional<String> row = fileLines.stream().skip(1).findFirst();
		if ( row.isPresent() ) {
			List<Double> inputList = new ArrayList<>();
			String[] cells = row.get().split( "," );
			int i = 1;
			while ( i < cells.length ) {
				inputList.add( Double.parseDouble( cells[i] ) );
				i += 8; // Expected columns between 'Area' columns - TODO actually check the headers
			}
			return inputList;
		} else {
			System.out.println( String.format( "Missing row in file %s", filename ) );
			return new ArrayList<>();
		}
	}
}
