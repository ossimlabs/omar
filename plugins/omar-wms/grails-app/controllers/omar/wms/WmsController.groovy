package omar.wms

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation

import omar.core.BindUtil
import omar.core.IpUtil

@Api( value = "wms",
		description = "WMS Support"
)
class WmsController
{
	def webMappingService
//	def springSecurityService
	def wmsLogService

	def index()
	{
		def wmsParams = params - params.subMap( [ 'controller', 'format' ] )
		def op = wmsParams.find { it.key.equalsIgnoreCase( 'request' ) }

		//println wmsParams

		switch ( op?.value?.toUpperCase() )
		{
		case "GETCAPABILITIES":
			forward action: 'getCapabilities'
			break
		case "GETMAP":
			forward action: 'getMap'
			break
		}
	}

	@ApiOperation( value = "Get the capabilities of the server", produces = 'application/vnd.ogc.wms_xml' )
	@ApiImplicitParams( [
			@ApiImplicitParam( name = 'service', value = 'OGC Service type', allowableValues = "[WMS]", defaultValue = 'WMS', paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.1.1, 1.3.0]", defaultValue = '1.3.0', paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required = true ),
	] )
	def getCapabilities( GetCapabilitiesRequest wmsParams )
	{
		BindUtil.fixParamNames( GetCapabilitiesRequest, params )
		bindData( wmsParams, params )

		def results = webMappingService.getCapabilities( wmsParams )

		render contentType: results.contentType, text: results.buffer

	}

	@ApiOperation( value = "Get image from the server", produces = 'application/xml,application/json' )
	@ApiImplicitParams( [
			@ApiImplicitParam( name = 'service', value = 'OGC service type', allowableValues = "[WMS]", defaultValue = 'WMS', paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.1.1, 1.3.0]", defaultValue = '1.3.0', paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[GetMap]", defaultValue = 'GetMap', paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'layers', value = 'Type name', defaultValue = "omar:raster_entry", paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'filter', value = 'Filter', paramType = 'query', dataType = 'string', required = false ),
			@ApiImplicitParam( name = 'srs', value = 'Spatial Reference System', defaultValue = "epsg:4326", paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'bbox', value = 'Bounding box', defaultValue = "-180,-90,180,90", paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'width', value = 'Width of result image', defaultValue = "1024", paramType = 'query', dataType = 'int', required = true ),
			@ApiImplicitParam( name = 'height', value = 'Height of result image', defaultValue = "512", paramType = 'query', dataType = 'int', required = true ),
			@ApiImplicitParam( name = 'format', value = 'MIME Type of result image', defaultValue = "image/jpeg", allowableValues = "[image/jpeg, image/png, image/gif]", paramType = 'query', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'styles', value = 'Styles to apply to image ', defaultValue = "", paramType = 'query', dataType = 'string', required = false ),
	] )
	def getMap( GetMapRequest wmsParams )
	{
		BindUtil.fixParamNames( GetMapRequest, params )
		bindData( wmsParams, params )

//		def results = webMappingService.getMap( wmsParams )
//
//		try{
//			render contentType: results.contentType, file: results.buffer
//			def otherParams = results.metrics
//
//			otherParams.ip = IpUtil.getClientIpAddr(request)
//			wmsLogService.logGetMapRequest( wmsParams, otherParams )
//			//println getClientIpAddr(request)
//
//		}
//		catch(e)
//		{
//			response.status = 404
//			render e.toString()
//		}

		// Getting the outputStream then testing in the finally will get rid
		// of the exceptions that state:
		//    Caused by: org.grails.gsp.GroovyPagesException: Error processing GroovyPageView: getOutputStream() has already been called for this response
		// After putting in the finally and the try catches you see the message is now trapped
		//
		def outputStream = null
		try
		{
			outputStream = response.outputStream
			def result = webMappingService.getMap( wmsParams )

			if(result.contentType) response.contentType = result.contentType
			if(result.buffer?.length) response.contentLength = result.buffer.length
			if(outputStream)
			{
				outputStream << result.buffer
			}

			def otherParams = result.metrics

			otherParams.ip = IpUtil.getClientIpAddr(request)
			otherParams.endDate = new Date()
			otherParams.endTime = System.currentTimeMillis()
			wmsLogService.logGetMapRequest( wmsParams, otherParams )
		}
		catch ( e )
		{
			log.debug(e.toString())
		}
		finally{
			if(outputStream!=null)
			{
				try{
					outputStream.close()
				}
				catch(e)
				{
					log.debug(e.toString())
				}
			}
		}


	}


}
