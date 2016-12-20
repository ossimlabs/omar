package time_lapse


class WmsRequest {

	String bbox	
	Date date
	String imageId
	String ipAddress
	String library

	
	static constraints = {
		date()
		imageId()
		bbox()
		library()
		ipAddress()
	}

	static mapping = {
		date index: "wms_request_date_idx"
		version false
	}
}
