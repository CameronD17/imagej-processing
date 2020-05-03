package data.overallaverage;

public enum RatGroup {

	M_CON_STR("Male Control Stress"),
	M_CON_NON("Male Control None"),
	M_PNS_STR("Male PNS Stress"),
	M_PNS_NON("Male PNS None"),
	F_CON_STR("Female Control Stress"),
	F_CON_NON("Female Control None"),
	F_PNS_STR("Female PNS Stress"),
	F_PNS_NON("Female PNS None");

	private String title;

	RatGroup( String title ) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public static RatGroup getGroupForId( int id ) {
		if ( id <= 10) return M_CON_STR;
		if ( id <= 20) return M_CON_NON;
		if ( id <= 30) return M_PNS_STR;
		if ( id <= 40) return M_PNS_NON;
		if ( id <= 50) return F_CON_STR;
		if ( id <= 60) return F_CON_NON;
		if ( id <= 70) return F_PNS_STR;
		if ( id <= 80) return F_PNS_NON;
		return null;
	}
}
