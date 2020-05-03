package inputconverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import data.FileFormats;
import data.FileType;
import data.density.Density;
import data.density.DensityImageData;

public class SectionAveragesInputConverter {

	private static final Pattern FILE_SECTION_NAME = Pattern.compile("BP(\\d+) s");

	public List<List<Density>> convertToGroupedDensities( List<String> fileLines, FileType fileType ) {
		List<Density> densities = convertToDensities( fileLines, fileType );
		List<List<Density>> groupedDensities = new ArrayList<>();
		List<Integer> groupNames = densities.stream()
				.map( density -> {
					Matcher matcher = FILE_SECTION_NAME.matcher( density.getCaption() );
					return matcher.find() ? Integer.parseInt( matcher.group( 1 ) ) : null;
				} )
				.filter( Objects::nonNull )
				.distinct()
				.sorted()
				.collect( Collectors.toList() );

		for ( Integer group : groupNames ) {
			groupedDensities.add ( densities.stream()
					.filter( d -> d.getCaption().startsWith( "BP" + group + " " ))
					.collect( Collectors.toList() ) );
		}

		for ( List<Density> densityGroup : groupedDensities ) {
			if ( densityGroup.size() == 4 ) {
				continue;
			}
			densityGroup.addAll( fillMissingDensities( densityGroup, fileType ) );
			densityGroup.sort( Comparator.comparing( Density::getCaption ) );
		}

		return groupedDensities;
	}


	private List<Density> convertToDensities( List<String> fileLines, FileType fileType ) {
		List<Density> densities = new ArrayList<>();

		final Integer expectedRows = FileFormats.SECTION_AVERAGES_EXPECTED_ROWS.get( fileType );
		final AtomicInteger counter = new AtomicInteger();
		final Collection<List<String>> chunkedToDensities = fileLines.stream()
				.collect( Collectors.groupingBy(it -> counter.getAndIncrement() / expectedRows ) )
				.values();

		for ( List<String> rows : chunkedToDensities ) {
			if ( !makesAValidChunk( rows, expectedRows ) ) {
				throw new IllegalArgumentException( "Unexpected format for input rows" );
			}
			densities.add( new Density.Builder()
					.caption( rows.get( 0 ).replaceAll("(,)*$", "") )
					.headers( rows.get( 1 ) )
					.densityEntries( constructDensityEntries( rows.subList( 2, rows.size() ) ) )
					.build()
			);
		}

		return densities;
	}

	private boolean makesAValidChunk( List<String> rows, Integer expectedRows ) {
		if ( rows.size() != expectedRows ) {
			System.out.println( String.format( "Incorrect number of rows found to construct a Density - expected %s, found %s (first row %s)", expectedRows, rows.size(), rows.get( 0 ) ) ) ;
			return false;
		}
		if ( !rows.get( 0 ).startsWith( "BP" ) || !rows.stream().skip( 1 ).allMatch( s -> s.startsWith( "," ) ) ) {
			System.out.println( String.format( "Incorrect format of rows to construct a Density found %s", rows ) ) ;
			return false;
		}
		return true;
	}

	private List<DensityImageData> constructDensityEntries( List<String> rawDataLines ) {
		List<DensityImageData> densityEntries = new ArrayList<>();

		for ( String line : rawDataLines ) {
			String[] values = line.split( "," );
			if ( values.length == 4 ) {
				densityEntries.add(
					new DensityImageData.Builder()
						.area( Double.valueOf( values[1] ) )
						.particles( Long.valueOf( values[2] ) )
						.density( Double.valueOf( values[3] ) )
						.build()
				);
			}
		}
		return densityEntries;
	}

	private List<Density> fillMissingDensities( List<Density> densityGroup, FileType fileType ) {
		final int imageDataMockCount = FileFormats.SECTION_AVERAGES_ROW_HEADERS.get( fileType ).size();
		final String densityHeader = densityGroup.get( 0 ).getCaption().split(" " )[0];

		List<Density> missingDensities = new ArrayList<>();
		for ( int i = 1; i <= 4; i++) {
			final String targetCaption = densityHeader + " s" + i;
			if ( densityGroup.stream().anyMatch( density -> density.getCaption().startsWith( targetCaption ) ) ) {
				continue;
			}
			System.out.println( String.format( "No data found for %s, filling with zeroes", targetCaption ) );
			List<DensityImageData> mockedDensityImageData = new ArrayList<>();
			for ( int j = 0; j < imageDataMockCount; j++ ) {
				mockedDensityImageData.add( new DensityImageData.Builder()
						.area( 0.0 )
						.particles( 0L )
						.density( 0.0 )
						.build());
			}

			missingDensities.add( new Density.Builder()
					.caption( targetCaption )
					.headers( "\n,Area,Particles,Density\n" )
					.densityEntries( mockedDensityImageData )
					.build()
			);
		}
		return missingDensities;
	}
}
