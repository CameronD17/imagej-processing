import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageJProcessing {

	private static final String FOLDER = "resources/";
	private static final String PARTICLES_SUFFIX = "-particles.csv";
	private static final String ROI_SUFFIX = "-ROI.csv";
	private static final String OUTPUT_FILE = "output/output.csv";

	private static ParticlesPerAreaCalculator calculator = new ParticlesPerAreaCalculator();

	public static void main (String[] args) {
		if ( !inputFilesHavePairs() ) {
			System.out.println( "ERROR: No pair for some files!" );
			return;
		}

		File csvOutputFile = new File(OUTPUT_FILE);
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			List<String> filenames = new ArrayList<>( getBareFileNames() );
			Collections.sort(filenames);
			for ( String filename : filenames ) {
				pw.append( filename ).append( "\n,Area,Particles,Density\n" );
				List<ImageData> calculations = calculator.calculate( FOLDER + filename + PARTICLES_SUFFIX, FOLDER + filename + ROI_SUFFIX );
				calculations.forEach( c -> pw.append(',')
						.append( c.getArea().toString() ).append(',')
						.append( c.getParticles().toString() ).append(',')
						.append( c.getDensity().toString() )
						.append( "\n" ) );
				pw.append( "\n" );
			}
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		}
	}

	private static boolean inputFilesHavePairs() {
		List<String> files = getAllFileNames();
		return getBareFileNames().stream().allMatch( s ->
			files.contains( FOLDER + s + PARTICLES_SUFFIX ) &&
			files.contains( FOLDER + s + ROI_SUFFIX ) );
	}

	private static List<String> getAllFileNames() {
		try ( Stream<Path> walk = Files.walk( Paths.get(FOLDER) ) ) {
			return walk.filter(Files::isRegularFile).map( Path::toString ).collect(Collectors.toList());
		} catch ( IOException e ) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private static Set<String> getBareFileNames() {
		return getAllFileNames().stream()
				.map( s -> s.replaceAll( "^" + FOLDER, "" )
						.replaceAll( PARTICLES_SUFFIX + "$", "" )
						.replaceAll( ROI_SUFFIX + "$", "" ) )
				.collect( Collectors.toSet() );
	}
}
