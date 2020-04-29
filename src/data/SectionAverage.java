package data;

import java.util.List;

public class SectionAverage {

	private final String header;
	private final FileType fileType;
	private final List<Region> rows;

	private SectionAverage( Builder builder ) {
		this.header = builder.header;
		this.fileType = builder.fileType;
		this.rows = builder.rows;
	}

	public String getHeader() {
		return header;
	}

	public FileType getFileType() {
		return fileType;
	}

	public List<Region> getRows() {
		return rows;
	}

	public static class Builder
	{
		private String header;
		private FileType fileType;
		private List<Region> rows;

		public Builder header( String header ) {
			this.header = header;
			return this;
		}

		public Builder fileType( FileType fileType ) {
			this.fileType = fileType;
			return this;
		}

		public Builder rows( List<Region> rows ) {
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
		for ( Region region : rows ) {
			printString.append( region.print() );
		}
		return printString.append( "\n" ).toString();
	}
}
