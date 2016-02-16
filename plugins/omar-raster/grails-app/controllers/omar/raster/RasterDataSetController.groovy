package omar.raster

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import omar.core.HttpStatusMessage

@Api( value = "dataManager",
		description = "DataManager Support"
)
class RasterDataSetController
{
	static allowedMethods = [
			addRaster: 'POST',
			removeRaster: 'POST'
	]

	def rasterDataSetService

	@ApiOperation( value = "Add a Raster to the database", produces = 'text/plain', httpMethod = 'POST' )
	@ApiImplicitParams( [
			@ApiImplicitParam( name = 'filename', value = 'Path to file to add', dataType = 'string', required = true )//,
//			@ApiImplicitParam(name = 'version', value = 'Version to request', allowableValues="[1.1.1]", defaultValue = '1.1.1', paramType = 'query', dataType = 'string', required=true),
//			@ApiImplicitParam(name = 'request', value = 'Request type', allowableValues="[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required=true),
	] )
	def addRaster()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = rasterDataSetService.addRaster( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}

	@ApiOperation( value = "Remove a Raster from the database", produces = 'text/plain', httpMethod = 'POST' )
	@ApiImplicitParams( [
			@ApiImplicitParam( name = 'filename', value = 'Path to file to remove', dataType = 'string', required = true )//,
//			@ApiImplicitParam(name = 'version', value = 'Version to request', allowableValues="[1.1.1]", defaultValue = '1.1.1', paramType = 'query', dataType = 'string', required=true),
//			@ApiImplicitParam(name = 'request', value = 'Request type', allowableValues="[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required=true),
	] )
	def removeRaster()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = rasterDataSetService.removeRaster( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}
}
