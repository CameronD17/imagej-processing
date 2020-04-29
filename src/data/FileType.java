package data;

public enum FileType {
	PFC("PFC"),
	PVN("PVN"),
	HIP("Hippocampus"),
	AMY("Amygdala");

	private String name;

	FileType ( String name ) {
		this.name = name;
	}

	public static FileType getValue( String input ) {
		FileType[] types = FileType.values();
		for ( final FileType type : types ) {
			if ( type.name.equals( input ) ) {
				return type;
			}
		}
		return null;
	}
}
