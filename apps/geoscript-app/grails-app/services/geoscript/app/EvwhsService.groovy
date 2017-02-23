package geoscript.app

import grails.transaction.Transactional
import geoscript.layer.WMS
import geoscript.layer.WMSLayer
import geoscript.render.Map as GeoScriptMap

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import org.springframework.beans.factory.annotation.Value

@Transactional(readOnly=true)
class EvwhsService
{
    @Value('${geoscript.evwhs.url}')
    String evwhsURL

    @Value('${geoscript.evwhs.username}')
    String evwhsUsername

    @Value('${geoscript.evwhs.password}')
    String evwhsPassword

    def wms(def params)
    {
      println (params + [url: evwhsURL, username: evwhsUsername, password: evwhsPassword])

      def wmsServer = evwhsURL
      def options = [user: evwhsUsername, password: evwhsPassword]
      def wms = new WMS(options, wmsServer)
      def layer = new WMSLayer( wms, ['DigitalGlobe:Imagery'] )
      def bbox = params?.find { it.key.equalsIgnoreCase('BBOX')}?.value?.split(',')?.collect { it?.toDouble() }
      def width = params?.find { it.key.equalsIgnoreCase('WIDTH')}?.value?.toInteger()
      def height = params?.find { it.key.equalsIgnoreCase('HEIGHT')}?.value?.toInteger()
      def srs = params.find { it.key.equalsIgnoreCase('CRS')}.value ?: params.find { it.equalsIgnoreCase('SRS')}?.value
      def ostream = new ByteArrayOutputStream()

      // println ([bbox, width, height, srs])

      def map = new GeoScriptMap(
          width: width,
          height: height,
          bounds: bbox,
          proj: srs,
          layers: [layer]
      )

      map.render(ostream)
      map.close()

      // def image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
      // ImageIO.write(image, 'png', ostream)

      [contentType: 'image/png', file: ostream.toByteArray()]
    }
}
