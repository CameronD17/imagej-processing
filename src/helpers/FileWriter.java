package helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class FileWriter {

	private final String rootFolder;
	private final String outputSubstring;

	public FileWriter( final String rootFolder, final String outputSubstring ) {
		this.rootFolder = rootFolder;
		this.outputSubstring = outputSubstring;
	}

	public void writeFile( final List<String> fileLines, final String filename ) {
		final File outputFile = new File( convertOutputFilename( filename ) );
		try ( PrintWriter printWriter = new PrintWriter( outputFile ) ) {
			for ( String line : fileLines ) {
				printWriter.append( line );
				printWriter.flush();
			}
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		}
	}

	private String convertOutputFilename( String rootFilename ) {
		final String filename = rootFilename.substring( 0, rootFilename.lastIndexOf('.') );
		final String fileExtension = rootFilename.substring(rootFilename.lastIndexOf('.') + 1);
		return rootFolder + filename + outputSubstring + "." + fileExtension;
	}
}
