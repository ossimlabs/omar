package time_lapse


import grails.transaction.Transactional
import javax.imageio.ImageIO


@Transactional
class ImageProxyService {

	def httpDownloadService


	def serviceMethod(params) {
		def url = params.url
		def inputStream = httpDownloadService.serviceMethod([
			password: params.password ?: null,
			url: url,
			username: params.username ?: null
		])
		//def byteArrayInputStream = new ByteArrayInputStream(inputStream)
		//def bufferedImage = ImageIO.read(byteArrayInputStream)


		//return bufferedImage
		return inputStream
	}
}
