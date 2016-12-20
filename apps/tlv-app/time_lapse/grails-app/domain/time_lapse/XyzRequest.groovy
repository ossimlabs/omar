package time_lapse


class XyzRequest {
	
	Date date
	String imageId
	String ipAddress
	String library
	Integer x
	Integer y
	Integer z


	static constraints = {
		date()
		imageId()
		x()
		y()
		z()
		library()
		ipAddress()
	}

	static mapping = {
		date index: "xyz_request_date_idx"
		version false
	}
}
