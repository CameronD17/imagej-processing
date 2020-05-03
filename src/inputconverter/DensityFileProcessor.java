package inputconverter;

import static processors.DensityProcessor.PARTICLES_SUFFIX;
import static processors.DensityProcessor.ROI_SUFFIX;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import helpers.FileReader;

public class DensityFileProcessor {

	private static final String ROOT_FOLDER = "resources/density/";
	private static final Pattern INPUT_PATTERN = Pattern.compile(".*\\.csv");

	private final FileReader fileReader = new FileReader( ROOT_FOLDER, INPUT_PATTERN );

	public Set<String> getBareFileNames() {
		return fileReader.getFileNames().stream()
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
	public List<Long> processParticlesFile( String filename ) {
		List<String> fileLines = fileReader.readFileLines( filename );

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
	public List<Double> processROIFile( String filename ) {
		List<String> fileLines = fileReader.readFileLines( filename );

		Optional<String> row = fileLines.stream().skip(1).findFirst();
		if ( row.isPresent() ) {
			List<Double> inputList = new ArrayList<>();
			String[] cells = row.get().split( "," );
			int i = 1;
			while ( i < cells.length ) {
				inputList.add( Double.parseDouble( cells[i] ) );
				i += 8; // Expected columns between 'Area' columns
			}
			return inputList;
		} else {
			System.out.println( String.format( "Missing row in file %s", filename ) );
			return new ArrayList<>();
		}
	}

	public boolean inputFilesHavePairs() {
		Set<String> files = fileReader.getFileNames();
		return files.stream()
				.filter( f -> f.contains(".gitignore") )
				.allMatch( s ->
						files.contains( s + PARTICLES_SUFFIX ) && files.contains( s + ROI_SUFFIX ) );
	}
}
