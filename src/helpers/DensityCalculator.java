package helpers;

import java.util.ArrayList;
import java.util.List;

import data.ImageData;

public class DensityCalculator {

	public List<ImageData> calculate( List<Long> particles, List<Double> areas ) {
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
