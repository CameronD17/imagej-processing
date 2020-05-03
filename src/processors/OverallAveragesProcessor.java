package processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import data.FileFormats;
import data.FileType;
import data.overallaverage.OverallAverageRegion;
import data.overallaverage.RatGroup;
import data.sectionaverage.SectionAverage;
import data.sectionaverage.SectionAverageRegion;
import helpers.FileReader;
import helpers.FileTypeHelper;
import helpers.FileWriter;
import inputconverter.OverallAveragesInputConverter;

public class OverallAveragesProcessor {

	private static final String ROOT_INPUT_FOLDER = "resources/overall";
	private static final String ROOT_OUTPUT_FOLDER = "output/overall";
	private static final String OUTPUT_SUBSTRING = "_overall";

	private static final Pattern INPUT_PATTERN = Pattern.compile("ISH.*_sections\\.csv");
	private static final Pattern FILE_TYPE = Pattern.compile("ISH.* ([a-zA-Z]+)_sections\\.csv");

	private final FileReader fileReader = new FileReader( ROOT_INPUT_FOLDER, INPUT_PATTERN );
	private final FileTypeHelper fileTypeHelper = new FileTypeHelper( FILE_TYPE );
	private final OverallAveragesInputConverter overallAveragesInputConverter = new OverallAveragesInputConverter();
	private final FileWriter fileWriter = new FileWriter( ROOT_OUTPUT_FOLDER, OUTPUT_SUBSTRING );

	public void process() {
		List<String> filenames = new ArrayList<>( fileReader.getFileNames() );
		Collections.sort( filenames );

		for ( String filename : filenames ) {
			List<String> outputLines = new ArrayList<>();
			final List<String> fileLines = fileReader.readFileLines( filename );
			final FileType fileType = fileTypeHelper.getFileType( filename );

			final List<String> columnHeaders = FileFormats.OVERALL_COLUMN_HEADERS.get( fileType );
			final List<SectionAverage> sectionAverages = overallAveragesInputConverter.convertToSectionAverages( fileLines, fileType );
			final Map<RatGroup, List<SectionAverage>> groupedSectionAverages = groupByRatGroup( sectionAverages );
			for ( Map.Entry<RatGroup, List<SectionAverage>> group : groupedSectionAverages.entrySet() ) {
				List<OverallAverageRegion> regions = new ArrayList<>();
				RatGroup ratGroup = group.getKey();
				outputLines.add( ratGroup.getTitle() + "," + String.join( ",", columnHeaders ) + "\n" );
				for ( SectionAverage average : group.getValue() ) {
					OverallAverageRegion region = convertSectionAverage( average );
					regions.add( region );
					outputLines.add( region.print() );
				}

				List<Double> nValues = new ArrayList<>();
				List<Double> aveValues = new ArrayList<>();
				List<Double> sdValues = new ArrayList<>();
				List<Double> semValues = new ArrayList<>();
				for ( int i = 0; i < columnHeaders.size(); i++ ) {
					double sum = 0.0;
					for ( OverallAverageRegion region : regions ) {
						double value = region.getAllAverages().get( i );
						sum += value;
					}
					// N value
					nValues.add( sum );

					// MEAN value
					double average = sum / (double) regions.size();
					aveValues.add( average );

					// Standard Deviation value
					double temp = 0;
					for ( OverallAverageRegion region : regions ) { // lol O(WTF?)
						double value = region.getAllAverages().get( i );
						double squrDiffToMean = Math.pow(value - average, 2);
						temp += squrDiffToMean;
					}
					double standardDeviation = Math.sqrt( temp / (double) regions.size() );
					sdValues.add( standardDeviation );

					// Standard Error of Mean value
					semValues.add( standardDeviation / Math.sqrt( average ) );
				}
				outputLines.add( "N," + nValues.stream().map( Object::toString ).collect(Collectors.joining(", ") )+ "\n");
				outputLines.add( "Average," + aveValues.stream().map( Object::toString ).collect(Collectors.joining(", ") )+ "\n" );
				outputLines.add( "SD," + sdValues.stream().map( Object::toString ).collect(Collectors.joining(", ") )+ "\n" );
				outputLines.add( "SEM," + semValues.stream().map( Object::toString ).collect(Collectors.joining(", ") )+ "\n" );
			}
			fileWriter.writeFile( outputLines, filename );
		}
	}

	private Map<RatGroup, List<SectionAverage>> groupByRatGroup( List<SectionAverage> allSectionAverages ) {
		return new TreeMap<>( allSectionAverages.stream()
			.collect( Collectors.groupingBy( density -> {
				Matcher matcher = Pattern.compile(",BP(\\d+) s").matcher( density.getHeader() );
				return matcher.find() ? RatGroup.getGroupForId( Integer.parseInt( matcher.group( 1 ) ) ) : null;
			} ) ) );
	}

	private OverallAverageRegion convertSectionAverage( final SectionAverage sectionAverage ) {
		List<SectionAverageRegion> regions = sectionAverage.getRows();

		Map<String, List<SectionAverageRegion>> averageGrps = regions.stream()
				.filter( Objects::nonNull )
				.collect( Collectors.groupingBy( sar -> sar.getHeader().substring( 0, sar.getHeader().length() - 2 ) ) );

		List<Double> averages = averageGrps.values().stream()
				.map( sar -> sar.stream()
						.map( SectionAverageRegion::getAverage )
						.mapToDouble( Double::doubleValue )
						.sum() / sar.size() )
				.collect( Collectors.toList() );

		return new OverallAverageRegion.Builder()
				.header( sectionAverage.getHeader().substring( 1, sectionAverage.getHeader().indexOf( " " ) ) )
				.groupedAverages( averages )
				.build();
	}
}
