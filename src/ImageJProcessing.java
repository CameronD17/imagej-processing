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

	private static final String FILE_SEPARATOR = "/";
	private static final String ROOT_FOLDER = "resources";
	private static final String PARTICLES_SUFFIX = "-particles.csv";
	private static final String ROI_SUFFIX = "-ROI.csv";
	private static final String OUTPUT_FILE = "output/%s-output.csv";

	private static ParticlesPerAreaCalculator calculator = new ParticlesPerAreaCalculator();

	public static void main (String[] args) {
		List<String> folders = new ArrayList<>( getFolderNames() );

		for ( String folder : folders ) {
			if ( folder.equals( "" ) ) {
				folder = "/";
			}

			if ( !inputFilesHavePairs( folder ) ) {
				System.out.println( String.format( "ERROR: No pair for some files in folder %s!", folder ) );
				continue;
			}

			File csvOutputFile = new File( String.format( OUTPUT_FILE, folder ) );
			try ( PrintWriter pw = new PrintWriter( csvOutputFile ) ) {
				List<String> filenames = new ArrayList<>( getBareFileNames( folder ) );
				Collections.sort( filenames );
				for ( String filename : filenames ) {
					pw.append( filename.replaceAll( FILE_SEPARATOR, "" ) ).append( "\n,Area,Particles,Density\n" );
					String particlesFilename = ROOT_FOLDER + FILE_SEPARATOR + folder + filename + PARTICLES_SUFFIX;
					String areasFilename = ROOT_FOLDER + FILE_SEPARATOR + folder  + filename + ROI_SUFFIX;
					List<ImageData> calculations = calculator.calculate( particlesFilename, areasFilename );
					calculations.forEach( c -> pw.append( ',' )
							.append( c.getArea().toString() )
							.append( ',' )
							.append( c.getParticles().toString() )
							.append( ',' )
							.append( c.getDensity().toString() )
							.append( "\n" ) );
					pw.append( "\n" );
				}
			} catch ( FileNotFoundException e ) {
				e.printStackTrace();
			}
		}
	}

	private static boolean inputFilesHavePairs( String folder ) {
		List<String> files = getAllFileNames( folder );
		return getBareFileNames( folder ).stream().allMatch( s ->
			files.contains( ROOT_FOLDER + folder + s + PARTICLES_SUFFIX ) &&
			files.contains( ROOT_FOLDER + folder + s + ROI_SUFFIX ) );
	}


	private static List<String> getFolderNames() {
		try ( Stream<Path> walk = Files.walk( Paths.get( ROOT_FOLDER ) ) ) {
			return walk.filter(Files::isDirectory)
					.map( Path::toString )
					.map( s -> s.replaceAll( "^" + ROOT_FOLDER, "" ) )
					.collect(Collectors.toList());
		} catch ( IOException e ) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private static List<String> getAllFileNames( String folder ) {
		try ( Stream<Path> walk = Files.walk( Paths.get( ROOT_FOLDER + folder ) ) ) {
			return walk.filter(Files::isRegularFile).map( Path::toString ).collect(Collectors.toList());
		} catch ( IOException e ) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private static Set<String> getBareFileNames( String folder ) {
		return getAllFileNames( folder ).stream()
				.map( s -> s.replaceAll( "^" + ROOT_FOLDER + folder, "" )
						.replaceAll( PARTICLES_SUFFIX + "$", "" )
						.replaceAll( ROI_SUFFIX + "$", "" ) )
				.collect( Collectors.toSet() );
	}
}
