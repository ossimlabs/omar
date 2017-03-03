package omar.wms

import grails.transaction.Transactional
import geoscript.geom.Bounds
import geoscript.proj.Projection

@Transactional
class WmsLogService
{

	def logGetMapRequest( GetMapRequest wmsParams, def otherParams )
	{
		def wmsLog = new WmsLog(
				request: wmsParams.request,
				layers: wmsParams.layers,
				bbox: wmsParams.bbox,
				width: wmsParams.width,
				height: wmsParams.height,
				format: wmsParams.format,
				styles: wmsParams.styles,
				startDate: otherParams.startDate,
				endDate: otherParams.endDate,
				ip: otherParams.ip
		)
		if((otherParams.internalTime!=null)&&
				(otherParams.startTime!=null)&&
				(otherParams.endTime!=null))
		{
			wmsLog.internalTime =  (otherParams.internalTime-otherParams.startTime)/1000
			wmsLog.renderTime   = (otherParams.endTime-otherParams.internalTime)/1000
			wmsLog.totalTime    = (otherParams.endTime-otherParams.startTime)/1000
		}

		def bounds = new Bounds( *( wmsParams?.bbox?.split( ',' )?.collect {
			it.toDouble()
		} ), wmsParams?.srs )

		def epsg3857 = ( bounds?.proj?.id == 3857 ) ? bounds : bounds?.reproject( 'epsg:3857' )

		wmsLog.geometry = bounds?.geometry?.g
		wmsLog.meanGsd = ( epsg3857?.maxY - epsg3857?.minY ) / wmsLog?.height
		wmsLog.geometry.setSRID( new Projection( wmsParams.srs )?.epsg )
		wmsLog.save()
	}
}
