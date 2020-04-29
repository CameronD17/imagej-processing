package processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import data.Density;
import data.FileType;
import data.ImageData;
import data.Region;
import data.SectionAverage;
import fileprocessor.SectionAveragesFileProcessor;

public class SectionAveragesProcessor {

	public static final String ROOT_FOLDER = "resources/sections";
	private static final String OUTPUT = "output/sections/output.csv";

	private static final Pattern FILE_SECTION_NAME = Pattern.compile("BP(\\d+) s");

	public static final Map<FileType, List<String>> ROW_HEADERS = Map.ofEntries(
			new AbstractMap.SimpleEntry<>( FileType.PFC, List.of( "IL R", "PrL R", "Cg1 R", "IL L", "PrL L", "Cg1 L" ) ),
			new AbstractMap.SimpleEntry<>( FileType.PVN, List.of( "Magno R", "Parvo R", "Magno L", "Magno L" ) ),
			new AbstractMap.SimpleEntry<>( FileType.HIP, List.of( "CA1 LA2", "CA2 L", "CA3 L", "DG L", "CA1 R", "CA2 R", "CA3 R", "DG R" ) ),
			new AbstractMap.SimpleEntry<>( FileType.AMY, List.of( "BLA RA2", "CeA R", "CoA R", "BMA R", "MeA R", "BLA L", "CeA L", "CoA L", "BMA L", "MeA L" ) )
	);

	public void process() {
		File csvOutputFile = new File( OUTPUT );
		try ( PrintWriter printWriter = new PrintWriter( csvOutputFile ) ) {
			List<String> filenames = new ArrayList<>( SectionAveragesFileProcessor.getBareFileNames() );
			Collections.sort( filenames );
			for ( String filename : filenames ) {
				FileType fileType = SectionAveragesFileProcessor.getFileType(filename);
				List<Density> allDensities = SectionAveragesFileProcessor.processDensityFile( ROOT_FOLDER + filename, fileType );
				List<List<Density>> groupedDensities = groupDensities( allDensities, fileType );
				for ( List<Density> densities : groupedDensities ) {
					String filePrefix = densities.get(0).getCaption().split( " " )[0];

					List<String> rowHeaders = ROW_HEADERS.get( fileType );

					List<Region> regions = new ArrayList<>();
					for ( int i = 0; i < rowHeaders.size(); i++ ) {
						regions.add( new Region.Builder()
								.header( rowHeaders.get( i ) )
								.s1Density( densities.get(0).getDensityEntries().get( i ).getDensity() )
								.s2Density( densities.get(1).getDensityEntries().get( i ).getDensity() )
								.s3Density( densities.get(2).getDensityEntries().get( i ).getDensity() )
								.s4Density( densities.get(3).getDensityEntries().get( i ).getDensity() )
								.build()
						);
					}

					SectionAverage sectionAverage = new SectionAverage.Builder()
							.fileType( fileType )
							.header( filePrefix )
							.rows( regions )
							.build();
					printWriter.append( sectionAverage.print() );
				}
			}
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		}
	}

	private List<List<Density>> groupDensities( List<Density> densities, FileType fileType ) {
		List<List<Density>> groupedDensities = new ArrayList<>();
		List<Integer> groupNames = densities.stream()
				.map( density -> {
					Matcher matcher = FILE_SECTION_NAME.matcher( density.getCaption() );
					return matcher.find() ? Integer.parseInt( matcher.group( 1 ) ) : null;
				} )
				.filter( Objects::nonNull )
				.distinct()
				.sorted()
				.collect( Collectors.toList() );

		for ( Integer group : groupNames ) {
			groupedDensities.add ( densities.stream()
					.filter( d -> d.getCaption().startsWith( "BP" + group + " " ))
					.collect( Collectors.toList() ) );
		}

		for ( List<Density> densityGroup : groupedDensities ) {
			if ( densityGroup.size() == 4 ) {
				continue;
			}
			densityGroup.addAll( fillMissingDensities( densityGroup, ROW_HEADERS.get( fileType ).size() ) );
			densityGroup.sort( Comparator.comparing( Density::getCaption ) );
		}

		return groupedDensities;
	}

	private List<Density> fillMissingDensities( List<Density> densityGroup, int imageDataMockCount ) {
		List<Density> missingDensities = new ArrayList<>();
		String densityHeader = densityGroup.get( 0 ).getCaption().split(" " )[0];
		for ( int i = 1; i <= 4; i++) {
			final String targetCaption = densityHeader + " s" + i;
			if ( densityGroup.stream().anyMatch( density -> density.getCaption().startsWith( targetCaption ) ) ) {
				continue;
			}
			System.out.println( String.format( "No data found for %s, filling with zeroes", targetCaption ) );
			List<ImageData> mockedImageData = new ArrayList<>();
			for ( int j = 0; j < imageDataMockCount; j++ ) {
				mockedImageData.add( new ImageData.Builder()
						.area( 0.0 )
						.particles( 0L )
						.density( 0.0 )
						.build());
			}

			missingDensities.add( new Density.Builder()
					.caption( targetCaption )
					.headers( "\n,Area,Particles,Density\n" )
					.densityEntries( mockedImageData )
					.build()
			);
		}
		return missingDensities;
	}
}
