import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParticlesPerAreaCalculator {

	public List<ImageData> calculate(String particlesFilename, String areasFilename) {
		List<Long> particles = processParticlesFile(particlesFilename);
		List<Double> areas = processROIFile(areasFilename);
		return divideParticlesByArea( particles, areas );
	}

	private List<Long> processParticlesFile(String filename) {
		List<Long> inputList = new ArrayList<>();
		try{
			File file = new File(filename);
			InputStream inputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			inputList = bufferedReader.lines().skip(1)
					.map( (line) -> new Long( line.split(",")[1] ) )
					.collect( Collectors.toList() );
			bufferedReader.close();
		} catch ( IOException e) {
			System.out.println( "Error reading particles file: " + e );
		}
		return inputList;
	}

	private List<Double> processROIFile(String filename) {
		List<Double> inputList = new ArrayList<>();
		try{
			File file = new File(filename);
			InputStream inputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String row = bufferedReader.lines().skip(1).findFirst().orElseThrow( RuntimeException::new );
			String[] cells = row.split( "," );
			int i = 1;
			while ( i < cells.length ) {
				inputList.add( new Double( cells[i] ) );
				i += 8; // Expected columns between 'Area' columns - TODO actually check the headers
			}
			bufferedReader.close();
		} catch ( IOException e) {
			System.out.println( "Error reading ROI file: " + e );
		}
		return inputList;
	}

	private List<ImageData> divideParticlesByArea( List<Long> particles, List<Double> areas ) {
		List<ImageData> data = new ArrayList<>();

		if ( particles.size() != areas.size() ) {
			throw new RuntimeException( String.format(
					"ERROR - list of particles (size: %s) is not the same size as list of areas (size: %s)",
					particles.size(), areas.size() ) );
		}

		for (int i = 0; i < particles.size(); i++) {
			Double area = areas.get( i );
			Long particle = particles.get( i );
			Double density = particle / area;
			data.add( new ImageData.Builder()
					.area( area )
					.particles( particle )
					.density( density )
					.build()
			);
		}
		return data;
	}
}
