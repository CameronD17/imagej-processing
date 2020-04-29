package processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Density;
import fileprocessor.DensityFileProcessor;
import helpers.DensityCalculator;

public class DensityProcessor {

	public static final String ROOT_FOLDER = "resources/density/";
	private static final String OUTPUT = "output/density/output.csv";

	public static final String PARTICLES_SUFFIX = "-particles.csv";
	public static final String ROI_SUFFIX = "-ROI.csv";

	private static DensityCalculator calculator = new DensityCalculator();

	public void process() {
		if ( !inputFilesHavePairs( ) ) {
			System.out.println( "ERROR: No pair for some files!" );
			return;
		}

		File csvOutputFile = new File( OUTPUT );
		try ( PrintWriter printWriter = new PrintWriter( csvOutputFile ) ) {
			List<String> filenames = new ArrayList<>( DensityFileProcessor.getBareFileNames() );
			Collections.sort( filenames );
			for ( String filename : filenames ) {
				String particlesFilename = ROOT_FOLDER + filename + PARTICLES_SUFFIX;
				String areasFilename = ROOT_FOLDER + filename + ROI_SUFFIX;
				Density density = new Density.Builder()
						.caption( filename )
						.headers( "\n,Area,Particles,Density\n" )
						.densityEntries( calculator.calculate(
								DensityFileProcessor.processParticlesFile( particlesFilename ),
								DensityFileProcessor.processROIFile( areasFilename ) ) )
						.build();
				printWriter.append( density.print() );
			}
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		}
	}

	private static boolean inputFilesHavePairs() {
		List<String> files = DensityFileProcessor.getAllFileNames();
		return DensityFileProcessor.getBareFileNames().stream()
				.filter( f -> f.contains(".gitignore") )
				.allMatch( s ->
				files.contains( ROOT_FOLDER + s + PARTICLES_SUFFIX ) &&
						files.contains( ROOT_FOLDER + s + ROI_SUFFIX ) );
	}
}
