package data.overallaverage;

import java.util.ArrayList;
import java.util.List;

public class OverallAverageRegion {

	private final String header;
	private final List<Double> groupedAverages;
	private final Double totalAverage;

	private OverallAverageRegion( Builder builder ) {
		this.header = builder.header;
		this.groupedAverages = builder.groupedAverages;

		// Nae need to average missing values
		Double sum = 0.0;
		int count = 0;
		if ( groupedAverages != null ) {
			for ( Double average : groupedAverages ) {
				if ( average != 0.0 ) {
					sum += average;
					count++;
				}
			}
		}
		this.totalAverage = count == 0 ? 0.0D : sum / count;
	}

	public String getHeader() {
		return header;
	}

	public List<Double> getGroupedAverages() {
		return groupedAverages;
	}

	public Double getTotalAverage() {
		return totalAverage;
	}

	public List<Double> getAllAverages() {
		List<Double> all = new ArrayList<> ( groupedAverages );
		all.add( totalAverage );
		return all;
	}

	public static class Builder
	{
		private String header;
		private List<Double> groupedAverages;

		public Builder header( String header ) {
			this.header = header;
			return this;
		}

		public Builder groupedAverages( List<Double> groupedAverages ) {
			this.groupedAverages = groupedAverages;
			return this;
		}

		public OverallAverageRegion build() {
			return new OverallAverageRegion(this);
		}
	}

	public String print() {
		StringBuilder averages = new StringBuilder( header + "," );
		for ( Double average: groupedAverages ) {
			averages.append( average );
			averages.append( "," );
		}
		averages.append( totalAverage );
		averages.append( "\n" );
		return averages.toString();
	}
}
