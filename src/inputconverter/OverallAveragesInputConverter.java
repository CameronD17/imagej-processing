package inputconverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import data.FileFormats;
import data.FileType;
import data.sectionaverage.SectionAverage;
import data.sectionaverage.SectionAverageRegion;

public class OverallAveragesInputConverter {

	public List<SectionAverage> convertToSectionAverages( List<String> fileLines, FileType fileType ) {
		List<SectionAverage> sectionAverages = new ArrayList<>();

		final Integer expectedRows = FileFormats.OVERALL_AVERAGES_EXPECTED_ROWS.get( fileType );
		final AtomicInteger counter = new AtomicInteger();
		final Collection<List<String>> chunkedToSectionAverages = fileLines.stream()
				.collect( Collectors.groupingBy(it -> counter.getAndIncrement() / expectedRows ) )
				.values();

		for ( List<String> rows : chunkedToSectionAverages ) {
			if ( !makesAValidChunk( rows, expectedRows ) ) {
				throw new IllegalArgumentException( "Unexpected format for input rows" );
			}
			sectionAverages.add( new SectionAverage.Builder()
					.header( rows.get( 0 ) )
					.rows( constructSectionAverageRegions( rows.subList( 1, rows.size() ) ) )
					.build()
			);
		}

		return sectionAverages;
	}

	private boolean makesAValidChunk( List<String> rows, Integer expectedRows ) {
		if ( rows.size() != expectedRows ) {
			System.out.println( String.format( "Incorrect number of rows found to construct a Section Average - expected %s, found %s (first row %s)", expectedRows, rows.size(), rows.get( 0 ) ) ) ;
			return false;
		}
		if ( !rows.get( 0 ).startsWith( ",BP" ) ) {
			System.out.println( String.format( "Incorrect format of rows to construct a Section Average found %s", rows ) ) ;
			return false;
		}
		return true;
	}

	private List<SectionAverageRegion> constructSectionAverageRegions( List<String> rawDataLines ) {
		List<SectionAverageRegion> sectionAverageRegions = new ArrayList<>();

		for ( String line : rawDataLines ) {
			String[] values = line.split( "," );
			if ( values.length == 6 ) {
				SectionAverageRegion region = new SectionAverageRegion.Builder()
						.header( values[0] )
						.s1Density( Double.valueOf( values[1] ) )
						.s2Density( Double.valueOf( values[2] ) )
						.s3Density( Double.valueOf( values[3] ) )
						.s4Density( Double.valueOf( values[4] ) )
						.build();
				// Sanity Check
				Double regionAverage = region.calculateAverage();
				if ( regionAverage.compareTo( Double.valueOf( values[5] ) ) != 0 ) {
					throw new IllegalArgumentException(
							String.format( "Calculated average (%s) does not match expected average (%s)!", regionAverage, values[5] ) );
				}
				sectionAverageRegions.add( region );
			}
		}
		return sectionAverageRegions;
	}
}
