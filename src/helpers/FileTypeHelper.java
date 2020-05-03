package helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.FileType;

public class FileTypeHelper {

	private final Pattern fileTypePattern;

	public FileTypeHelper ( final Pattern fileTypePattern ) {
		this.fileTypePattern = fileTypePattern;
	}

	public FileType getFileType( String filename ) {
		Matcher matcher = fileTypePattern.matcher( filename );
		if ( matcher.find() ) {
			return FileType.getValue( matcher.group( 1 ) );
		}
		System.out.println( String.format( "Filename %s does not match the expected format - skipping file", filename ) );
		return null;
	}
}
