package fileprocessor;

import static processors.SectionAveragesProcessor.ROOT_FOLDER;
import static processors.SectionAveragesProcessor.ROW_HEADERS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import data.Density;
import data.FileType;
import data.ImageData;
import helpers.FileReader;

public class SectionAveragesFileProcessor {

	private static final Pattern INPUT_PATTERN = Pattern.compile("ISH.*\\.csv");
	private static final Pattern FILE_TYPE = Pattern.compile("ISH.* ([a-zA-Z]+)\\.csv");

	// Expected density values, plus a title, a header, and an extra buffer row (3 extra rows)
	private static final int EXTRA_ROWS = 3;
	private static final Map<FileType, Integer> EXPECTED_ROWS = Map.ofEntries(
			new AbstractMap.SimpleEntry<>( FileType.PFC, ROW_HEADERS.get( FileType.PFC ).size() + EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.PVN, ROW_HEADERS.get( FileType.PVN ).size() + EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.HIP, ROW_HEADERS.get( FileType.HIP ).size() + EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.AMY, ROW_HEADERS.get( FileType.AMY ).size() + EXTRA_ROWS )
	);

	public static Set<String> getBareFileNames() {
		return getAllFileNames().stream()
				.map( s -> s.replaceAll( "^" + ROOT_FOLDER, "" ) )
				.collect( Collectors.toSet() );
	}

	public static FileType getFileType( String filename ) {
		Matcher matcher = FILE_TYPE.matcher( filename );
		if ( matcher.find() ) {
			return FileType.getValue( matcher.group( 1 ) );
		}
		System.out.println( String.format( "Filename %s does not match the expected format - skipping file", filename ) );
		return null;
	}

	public static List<Density> processDensityFile( String filename, FileType fileType ) {
		List<Density> densities = new ArrayList<>();

		final List<String> fileLines = FileReader.readFileLines( filename );
		final Integer expectedRows = EXPECTED_ROWS.get( fileType );
		final AtomicInteger counter = new AtomicInteger();
		final Collection<List<String>> chunkedToDensities = fileLines.stream()
				.collect( Collectors.groupingBy(it -> counter.getAndIncrement() / expectedRows ) )
				.values();

		for ( List<String> rows : chunkedToDensities ) {
			if ( makesAValidChunk( rows, expectedRows ) ) {
				densities.add( new Density.Builder()
						.caption( rows.get( 0 ).replaceAll("(,)*$", "") )
						.headers( rows.get( 1 ) )
						.densityEntries( rows.stream().skip( 2 ) // Skip Caption + Headers
								.map( data -> {
									String[] values = data.split( "," );
									if ( values.length != 4 )
										return null; // We expect an empty cell + the three values as per ImageData.print()
									return new ImageData.Builder()
											.area( Double.valueOf( values[1] ) )
											.particles( Long.valueOf( values[2] ) )
											.density( Double.valueOf( values[3] ) )
											.build();
								} )
								.filter( Objects::nonNull )
								.collect( Collectors.toList() ) )
						.build() );
			}
		}

		return densities;
	}

	private static List<String> getAllFileNames() {
		try ( Stream<Path> walk = Files.walk( Paths.get( ROOT_FOLDER ) ) ) {
			return walk.filter(Files::isRegularFile)
					.map( Path::toString )
					.filter( INPUT_PATTERN.asPredicate() )
					.collect( Collectors.toList());
		} catch ( IOException e ) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private static boolean makesAValidChunk( List<String> rows, Integer expectedRows ) {
		if ( rows.size() != expectedRows ) {
			System.out.println( String.format( "Incorrect number of rows found to construct a Density - expected %s, found %s (first row %s)", expectedRows, rows.size(), rows.get( 0 ) ) ) ;
			return false;
		}

		if ( !( rows.get( 0 ).startsWith( "BP" ) || !rows.stream().skip( 1 ).allMatch( s -> s.startsWith( "," ) ) ) ) {
			System.out.println( String.format( "Incorrect format of rows to construct a Density found %s", rows ) ) ;
			return false;
		}
		return true;
	}
}
