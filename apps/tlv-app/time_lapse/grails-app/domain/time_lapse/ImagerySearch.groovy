package time_lapse


class ImagerySearch {

	Date date
	Date endDate
	String ipAddress
	String library
	String location
	Integer maxCloudCover
	Integer maxResults
	Double minNiirs
	String sensors
	Date startDate	


	static constraints = {
		date()
		location()
		library()
		startDate()
		endDate()
		sensors()
		ipAddress()
	}

	static mapping = {
		date index: "imagery_search_date_idx"
		version false
	}
}
