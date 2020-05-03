package processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import data.FileFormats;
import data.FileType;
import data.density.Density;
import data.sectionaverage.SectionAverage;
import data.sectionaverage.SectionAverageRegion;
import helpers.FileReader;
import helpers.FileTypeHelper;
import helpers.FileWriter;
import inputconverter.SectionAveragesInputConverter;

public class SectionAveragesProcessor {

	private static final String ROOT_INPUT_FOLDER = "resources/sections";
	private static final String ROOT_OUTPUT_FOLDER = "output/sections";
	private static final String OUTPUT_SUBSTRING = "_sections";
	private static final Pattern INPUT_PATTERN = Pattern.compile("ISH.*\\.csv");
	private static final Pattern FILE_TYPE = Pattern.compile("ISH.* ([a-zA-Z]+)\\.csv");

	private final FileReader fileReader = new FileReader( ROOT_INPUT_FOLDER, INPUT_PATTERN );
	private final FileTypeHelper fileTypeHelper = new FileTypeHelper( FILE_TYPE );
	private final SectionAveragesInputConverter sectionAveragesProcessor = new SectionAveragesInputConverter();
	private final FileWriter fileWriter = new FileWriter( ROOT_OUTPUT_FOLDER, OUTPUT_SUBSTRING );

	public void process() {
		List<String> filenames = new ArrayList<>( fileReader.getFileNames() );
		Collections.sort( filenames );

		for ( String filename : filenames ) {
			List<String> outputLines = new ArrayList<>();
			final List<String> fileLines = fileReader.readFileLines( filename );
			final FileType fileType = fileTypeHelper.getFileType( filename );

			final List<String> rowHeaders = FileFormats.SECTION_AVERAGES_ROW_HEADERS.get( fileType );
			final List<List<Density>> groupedDensities =
					sectionAveragesProcessor.convertToGroupedDensities( fileLines, fileType );

			for ( List<Density> densities : groupedDensities ) {
				String filePrefix = densities.get( 0 ).getCaption().split( " " )[0];

				List<SectionAverageRegion> sectionAverageRegions = new ArrayList<>();
				for ( int i = 0; i < rowHeaders.size(); i++ ) {
					sectionAverageRegions.add( new SectionAverageRegion.Builder().header( rowHeaders.get( i ) )
							.s1Density( densities.get( 0 ).getDensityEntries().get( i ).getDensity() )
							.s2Density( densities.get( 1 ).getDensityEntries().get( i ).getDensity() )
							.s3Density( densities.get( 2 ).getDensityEntries().get( i ).getDensity() )
							.s4Density( densities.get( 3 ).getDensityEntries().get( i ).getDensity() )
							.build() );
				}

				SectionAverage sectionAverage = new SectionAverage.Builder()
						.header( filePrefix )
						.rows( sectionAverageRegions )
						.build();
				outputLines.add( sectionAverage.print() );
			}
			fileWriter.writeFile( outputLines, filename );
		}
	}
}
