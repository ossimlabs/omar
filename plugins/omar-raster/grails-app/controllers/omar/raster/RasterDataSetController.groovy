package omar.raster

import omar.core.HttpStatusMessage

class RasterDataSetController
{
	def rasterDataSetService

	def addRaster()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = rasterDataSetService.addRaster( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}

	def removeRaster()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = rasterDataSetService.removeRaster( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}
}
