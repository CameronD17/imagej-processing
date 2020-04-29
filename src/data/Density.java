package data;

import java.util.List;

public class Density {

	private final String caption;
	private final String headers;
	private final List<ImageData> densityEntries;

	private Density( Builder builder ) {
		this.caption = builder.caption;
		this.headers = builder.headers;
		this.densityEntries = builder.densityEntries;
	}

	public String getCaption() {
		return caption;
	}

	public String getHeaders() {
		return headers;
	}

	public List<ImageData> getDensityEntries() {
		return densityEntries;
	}

	public void addEntry( ImageData entry ) {
		densityEntries.add( entry );
	}

	public static class Builder
	{
		private String caption;
		private String headers;
		private List<ImageData> densityEntries;

		public Builder caption( String caption ) {
			this.caption = caption;
			return this;
		}

		public Builder headers( String headers ) {
			this.headers = headers;
			return this;
		}

		public Builder densityEntries( List<ImageData> densityEntries ) {
			this.densityEntries = densityEntries;
			return this;
		}

		public Density build() {
			return new Density(this);
		}
	}

	public String print() {
		StringBuilder printString = new StringBuilder();
		printString.append( caption ).append( headers );
		for ( ImageData data : densityEntries ) {
			printString.append( data.print() );
		}
		return printString.append( "\n" ).toString();
	}
}
