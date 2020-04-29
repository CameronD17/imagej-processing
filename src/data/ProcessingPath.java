package data;

public enum ProcessingPath {
	DENSITY(1),
	SECTION_AVERAGES(2),
	OVERALL_AVERAGES(3);

	private int path;

	ProcessingPath( int path ) {
		this.path = path;
	}

	public static ProcessingPath getValue( int id ) {
		ProcessingPath[] paths = ProcessingPath.values();
		for ( final ProcessingPath processingPath : paths ) {
			if ( processingPath.path == id ) {
				return processingPath;
			}
		}
		return null;
	}
}
