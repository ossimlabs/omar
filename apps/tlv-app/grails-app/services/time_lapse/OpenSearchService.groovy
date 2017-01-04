package time_lapse


import grails.transaction.Transactional
import java.awt.image.BufferedImage
import java.awt.Image
import javax.imageio.ImageIO


@Transactional
class OpenSearchService {

	def grailsApplication
	def assetResourceLocator


	def serviceMethod() {
		def tlvLogo = assetResourceLocator.findAssetForURI( "logos/tlv.png" )

		// scale a copy of the logo
		def inputStream = new BufferedInputStream( tlvLogo?.inputStream ?: this.getClass().getClassLoader().getResourceAsStream( "assets/logos/tlv.png" ) )
		def originalImage = ImageIO.read( inputStream);
		def scaledImage = originalImage.getScaledInstance( 16, 16, Image.SCALE_SMOOTH )

		// create a base 64 representation of the icon
		def iconImage = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_ARGB )
		def graphic = iconImage.createGraphics()
		graphic.drawImage(scaledImage, 0, 0, null)
		graphic.dispose()

     	def byteArrayOutputStream = new ByteArrayOutputStream()
		ImageIO.write( iconImage, "png", byteArrayOutputStream )
		def base64 = byteArrayOutputStream.toByteArray().encodeBase64()

		// write the xml
		def baseUrl = grailsApplication.config.baseUrl
		def contextPath = grailsApplication.config.server.contextPath ?: ""
		def xml = new StringBuffer()
		xml.append( '<?xml version = "1.0" encoding = "UTF-8"?>' )
		xml.append( '<OpenSearchDescription xmlns = "http://a9.com/-/spec/opensearch/1.1/">' )
		xml.append( '	<ShortName>TLV</ShortName>' )
		xml.append( '	<Description>Time Lapse Viewer</Description>' )
		xml.append( '	<Image height = "16" width = "16">' )
		xml.append( "		data:image/x-icon;base64,${base64} ")
		xml.append( '	</Image>' )
		xml.append( '	<InputEncoding>UTF-8</InputEncoding>' )
		xml.append( "	<Url template = \"${ baseUrl }${ contextPath }/?location={searchTerms}\" type = \"text/html\"/>" )
		xml.append( '</OpenSearchDescription>' )


		return xml.toString()
	}
}
