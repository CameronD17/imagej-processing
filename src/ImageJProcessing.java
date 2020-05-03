import data.ProcessingPath;
import processors.DensityProcessor;
import processors.OverallAveragesProcessor;
import processors.SectionAveragesProcessor;

public class ImageJProcessing {

	public static void main ( String[] args ) {

		final ProcessingPath path = parseArgs(args);
		final DensityProcessor densityProcessor = new DensityProcessor();
		final SectionAveragesProcessor sectionAveragesProcessor = new SectionAveragesProcessor();
		final OverallAveragesProcessor overallAveragesProcessor = new OverallAveragesProcessor();

		System.out.println( "Processing " + path + " path." );

		switch ( path ) {
		case DENSITY:
			densityProcessor.process();
			break;
		case SECTION_AVERAGES:
			sectionAveragesProcessor.process();
			break;
		case OVERALL_AVERAGES:
			overallAveragesProcessor.process();
			break;
		default:
			break;
		}
	}

	private static ProcessingPath parseArgs ( String[] args ) {
		if ( args.length != 1 ) {
			throw new IllegalArgumentException( String.format( "Exactly one numerical argument required! Found %s", args.length ) );
		}

		final ProcessingPath processingPath;

		try {
			int path = Integer.parseInt( args[0] );
			processingPath = ProcessingPath.getValue( path );
			if ( processingPath == null ) {
				throw new IllegalArgumentException( String.format( "No processing path found for input '%s'", path ) );
			}
		} catch ( NumberFormatException nfe ) {
			throw new IllegalArgumentException( String.format( "Argument passed must be numerical! Found '%s'", args[0] ) );
		}

		return processingPath;
	}
}
