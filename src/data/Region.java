package data;

public class Region {

	private final String header;
	private final Double s1Density;
	private final Double s2Density;
	private final Double s3Density;
	private final Double s4Density;
	private final Double average;

	private Region( Builder builder ) {
		this.header = builder.header;
		this.s1Density = builder.s1Density;
		this.s2Density = builder.s2Density;
		this.s3Density = builder.s3Density;
		this.s4Density = builder.s4Density;

		// Nae need to average missing values
		int count = 0;
		if ( s1Density != 0.0 ) count++;
		if ( s2Density != 0.0 ) count++;
		if ( s3Density != 0.0 ) count++;
		if ( s4Density != 0.0 ) count++;
		this.average = count == 0 ? 0.0D : ( s1Density + s2Density + s3Density + s4Density ) / count;
	}

	public String getHeader() {
		return header;
	}

	public Double getS1Density() {
		return s1Density;
	}

	public Double getS2Density() {
		return s2Density;
	}

	public Double getS3Density() {
		return s3Density;
	}

	public Double getS4Density() {
		return s4Density;
	}

	public Double getAverage() {
		return average;
	}

	public static class Builder
	{
		private String header;
		private Double s1Density;
		private Double s2Density;
		private Double s3Density;
		private Double s4Density;

		public Builder header( String header ) {
			this.header = header;
			return this;
		}

		public Builder s1Density( Double s1Density ) {
			this.s1Density = s1Density;
			return this;
		}

		public Builder s2Density( Double s2Density ) {
			this.s2Density = s2Density;
			return this;
		}

		public Builder s3Density( Double s3Density ) {
			this.s3Density = s3Density;
			return this;
		}

		public Builder s4Density( Double s4Density ) {
			this.s4Density = s4Density;
			return this;
		}

		public Region build() {
			return new Region(this);
		}
	}

	public String print() {
		return header + "," + s1Density + "," + s2Density + "," + s3Density + "," + s4Density + "," + average + "\n";
	}
}
