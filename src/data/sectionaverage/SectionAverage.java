package data.sectionaverage;

import java.util.List;

public class SectionAverage {

	private final String header;
	private final List<SectionAverageRegion> rows;

	private SectionAverage( Builder builder ) {
		this.header = builder.header;
		this.rows = builder.rows;
	}

	public String getHeader() {
		return header;
	}

	public List<SectionAverageRegion> getRows() {
		return rows;
	}

	public static class Builder
	{
		private String header;
		private List<SectionAverageRegion> rows;

		public Builder header( String header ) {
			this.header = header;
			return this;
		}

		public Builder rows( List<SectionAverageRegion> rows ) {
			this.rows = rows;
			return this;
		}

		public SectionAverage build() {
			return new SectionAverage(this);
		}
	}

	public String print() {
		StringBuilder printString = new StringBuilder();
		String headerRow = "," + header + " s1," + header + " s2," + header + " s3," + header + " s4,all sections mean\n";
		printString.append( headerRow );
		for ( SectionAverageRegion sectionAverageRegion : rows ) {
			printString.append( sectionAverageRegion.print() );
		}
		return printString.append( "\n" ).toString();
	}
}
