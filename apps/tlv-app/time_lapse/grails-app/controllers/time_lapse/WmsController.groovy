package time_lapse


class WmsController {

	def logsService
	def wmsConversionService


	def index() {
		logsService.recordWmsRequest(params, request)
		def image = wmsConversionService.serviceMethod(params)


		if (image.class.toString().contains("String")) { redirect(url: image) }
		else {
			response.contentType = "image/png"
			response.outputStream << image
			response.outputStream.flush()
			response.outputStream.close()
		}
	}
}
