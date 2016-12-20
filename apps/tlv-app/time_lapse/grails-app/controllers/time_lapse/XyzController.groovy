package time_lapse


class XyzController {

	def logsService
	def xyzConversionService


	def index() {
		logsService.recordXyzRequest(params, request)
		def image = xyzConversionService.serviceMethod(params)


		if (image.class.toString().contains("String")) { redirect(url: image) }
		else {
			response.contentType = "image/png"
			response.outputStream << image
			response.outputStream.flush()
			response.outputStream.close()
		}
	}
}
