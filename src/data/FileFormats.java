package data;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class FileFormats {

	public static final Map<FileType, List<String>> SECTION_AVERAGES_ROW_HEADERS = Map.ofEntries(
			new AbstractMap.SimpleEntry<>( FileType.PFC, List.of( "IL R", "PrL R", "Cg1 R", "IL L", "PrL L", "Cg1 L" ) ),
			new AbstractMap.SimpleEntry<>( FileType.PVN, List.of( "Magno R", "Parvo R", "Magno L", "Parvo L" ) ),
			new AbstractMap.SimpleEntry<>( FileType.HIP, List.of( "CA1 L", "CA2 L", "CA3 L", "DG L", "CA1 R", "CA2 R", "CA3 R", "DG R" ) ),
			new AbstractMap.SimpleEntry<>( FileType.AMY, List.of( "BLA R", "CeA R", "CoA R", "BMA R", "MeA R", "BLA L", "CeA L", "CoA L", "BMA L", "MeA L" ) )
	);

	// Expected density values, plus a title/caption and a header (2 extra rows)
	private static final int SECTION_AVERAGES_EXTRA_ROWS = 2;
	public static final Map<FileType, Integer> SECTION_AVERAGES_EXPECTED_ROWS = Map.ofEntries(
			new AbstractMap.SimpleEntry<>( FileType.PFC, SECTION_AVERAGES_ROW_HEADERS.get( FileType.PFC ).size() + SECTION_AVERAGES_EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.PVN, SECTION_AVERAGES_ROW_HEADERS.get( FileType.PVN ).size() + SECTION_AVERAGES_EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.HIP, SECTION_AVERAGES_ROW_HEADERS.get( FileType.HIP ).size() + SECTION_AVERAGES_EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.AMY, SECTION_AVERAGES_ROW_HEADERS.get( FileType.AMY ).size() + SECTION_AVERAGES_EXTRA_ROWS )
	);

	// Expected section average values, plus a title/caption (1 extra row)
	private static final int OVERALL_EXTRA_ROWS = 1;
	public static final Map<FileType, Integer> OVERALL_AVERAGES_EXPECTED_ROWS = Map.ofEntries(
			new AbstractMap.SimpleEntry<>( FileType.PFC, SECTION_AVERAGES_ROW_HEADERS.get( FileType.PFC ).size() + OVERALL_EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.PVN, SECTION_AVERAGES_ROW_HEADERS.get( FileType.PVN ).size() + OVERALL_EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.HIP, SECTION_AVERAGES_ROW_HEADERS.get( FileType.HIP ).size() + OVERALL_EXTRA_ROWS ),
			new AbstractMap.SimpleEntry<>( FileType.AMY, SECTION_AVERAGES_ROW_HEADERS.get( FileType.AMY ).size() + OVERALL_EXTRA_ROWS )
	);

	public static final Map<FileType, List<String>> OVERALL_COLUMN_HEADERS = Map.ofEntries(
			new AbstractMap.SimpleEntry<>( FileType.PFC, List.of( "IL R+L mean", "PrL R+L mean", "Cg1 R+L mean", "Total mean" ) ),
			new AbstractMap.SimpleEntry<>( FileType.PVN, List.of( "Magno R+L mean", "Parvo R+L mean", "Total mean" ) ),
			new AbstractMap.SimpleEntry<>( FileType.HIP, List.of( "CA1 R+L mean", "CA2 R+L mean", "CA3 R+L mean", "DG R+L mean", "Total mean" ) ),
			new AbstractMap.SimpleEntry<>( FileType.AMY, List.of( "BLA R+L mean", "CeA R+L mean", "CoA R+L mean", "BMA R+L mean", "MeA R+L mean", "Total mean" ) )
	);
}
