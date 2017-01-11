package omar.wms

import com.vividsolutions.jts.geom.Geometry
//import org.hibernate.spatial.GeometryType

class WmsLog
{

	Long width
	Long height
	String layers
	String styles
	String format
	String request
	String bbox
	Double internalTime
	Double renderTime
	Double totalTime
	Date startDate
	Date endDate
	String userName
	String ip
	String url
	Double meanGsd
	Geometry geometry

	static constraints = {
		width( nullable: true )
		height( nullable: true )
		layers( nullable: true )
		styles( nullable: true )
		format( nullable: true )
		request( nullable: true )
		bbox( nullable: true )
		internalTime( nullable: true )
		renderTime( nullable: true )
		totalTime( nullable: true )
		startDate( nullable: true )
		endDate( nullable: true )
		userName( nullable: true )
		ip( nullable: true )
		url( nullable: true )
		meanGsd( nullable: true )
		geometry( nullable: true )
	}
	static mapping = {
      		cache true
      		id generator: 'identity'
		version false
		url type: 'text'
		layers type: 'text'
		geometry /*type: GeometryType, */ sqlType: 'geometry(polygon, 4326)'
	}
}
