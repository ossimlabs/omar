package omar.video

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import omar.core.HttpStatusMessage

@Api( value = "dataManager",
		description = "DataManager Support"
)
class VideoDataSetController
{
	static allowedMethods = [
		addVideo: 'POST',
		removeVideo: 'POST'
	]

	def videoDataSetService

	@ApiOperation( value = "Add a Video to the database", produces = 'text/plain', httpMethod = 'POST' )
	@ApiImplicitParams( [
			@ApiImplicitParam( name = 'filename', value = 'Path to file to add', dataType = 'string', required = true )//,
//			@ApiImplicitParam(name = 'version', value = 'Version to request', allowableValues="[1.1.1]", defaultValue = '1.1.1', paramType = 'query', dataType = 'string', required=true),
//			@ApiImplicitParam(name = 'request', value = 'Request type', allowableValues="[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required=true),
	] )
	def addVideo()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = videoDataSetService.addVideo( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}

	@ApiOperation( value = "Add a Video to the database", produces = 'text/plain', httpMethod = 'POST' )
	@ApiImplicitParams( [
			@ApiImplicitParam( name = 'filename', value = 'Path to file to add', dataType = 'string', required = true )//,
//			@ApiImplicitParam(name = 'version', value = 'Version to request', allowableValues="[1.1.1]", defaultValue = '1.1.1', paramType = 'query', dataType = 'string', required=true),
//			@ApiImplicitParam(name = 'request', value = 'Request type', allowableValues="[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required=true),
	] )
	def removeVideo()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = videoDataSetService.removeVideo( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}
}
