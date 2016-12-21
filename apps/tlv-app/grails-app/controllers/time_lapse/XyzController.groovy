package time_lapse


class XyzController {

	def xyzConversionService


	def index() {
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
