package processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.density.Density;
import data.density.DensityImageData;
import helpers.FileWriter;
import inputconverter.DensityFileProcessor;

public class DensityProcessor {

	public static final String PARTICLES_SUFFIX = "-particles.csv";
	public static final String ROI_SUFFIX = "-ROI.csv";

	private static final String ROOT_OUTPUT_FOLDER = "output/density/";
	private static final String OUTPUT_SUBSTRING = "output";

	private final DensityFileProcessor densityFileProcessor = new DensityFileProcessor();
	private final FileWriter fileWriter = new FileWriter( ROOT_OUTPUT_FOLDER, OUTPUT_SUBSTRING );

	public void process() {
		if ( !densityFileProcessor.inputFilesHavePairs() ) {
			System.out.println( "ERROR: No pair for some files!" );
			return;
		}
		List<String> outputLines = new ArrayList<>();
		List<String> filenames = new ArrayList<>( densityFileProcessor.getBareFileNames() );
		Collections.sort( filenames );
		for ( String filename : filenames ) {
			List<Long> particles = densityFileProcessor.processParticlesFile( filename + PARTICLES_SUFFIX );
			List<Double> areas = densityFileProcessor.processROIFile( filename + ROI_SUFFIX );
			Density density = new Density.Builder()
					.caption( filename )
					.headers( "\n,Area,Particles,Density\n" )
					.densityEntries( calculate( particles, areas ) )
					.build();
			outputLines.add( density.print() );
		}
		fileWriter.writeFile( outputLines, ".csv" );
	}

	private List<DensityImageData> calculate( List<Long> particles, List<Double> areas ) {
		List<DensityImageData> data = new ArrayList<>();

		if ( particles.size() != areas.size() ) {
			throw new RuntimeException( String.format(
					"ERROR - list of particles (size: %s) is not the same size as list of areas (size: %s)",
					particles.size(), areas.size() ) );
		}

		for (int i = 0; i < particles.size(); i++) {
			Double area = areas.get( i );
			Long particle = particles.get( i );
			Double density = particle / area;
			data.add( new DensityImageData.Builder()
					.area( area )
					.particles( particle )
					.density( density )
					.build()
			);
		}
		return data;
	}
}
