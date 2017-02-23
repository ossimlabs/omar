package geoscript.app

import grails.transaction.Transactional
import geoscript.layer.WMS
import geoscript.layer.WMSLayer
import geoscript.render.Map as GeoScriptMap

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@Transactional(readOnly=true)
class EvwhsService
{
    def wms(def params)
    {
      println params

      def wmsServer = 'https://evwhs.digitalglobe.com/mapservice/wmsaccess?connectid=eb33ba21-1782-4ffc-8a5c-4854e21effb9&version=1.3.0&request=GetCapabilities'
      def options = [user: 'kfeldbush', password: 'P@ssw0rdP@ssw0rd']
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

    def blah(def params)
    {
      def wmsServer = 'https://evwhs.digitalglobe.com/mapservice/wmsaccess?connectid=eb33ba21-1782-4ffc-8a5c-4854e21effb9&version=1.3.0&request=GetCapabilities'
      def options = [user: 'kfeldbush', password: 'P@ssw0rdP@ssw0rd']
      def wms = new WMS(options, wmsServer)
      def layer = new WMSLayer( wms, ['DigitalGlobe:Imagery'] )
      def width = params?.find { it.key.equalsIgnoreCase('WIDTH')}?.value?.toInteger()
      def height = params?.find { it.key.equalsIgnoreCase('HEIGHT')}?.value?.toInteger()
      def srs = params.find { it.key.equalsIgnoreCase('CRS')}.value ?: params.find { it.equalsIgnoreCase('SRS')}?.value

      def map = new GeoScriptMap(
          width: width,
          height: height,
          bounds: [3169996.4370428296,528332.739507138,3179780.376663332,538116.6791276409],
          proj: srs,
          layers: [layer]
      )

      def ostream = new ByteArrayOutputStream()

      map.render(ostream)
      map.close()

      [contentType: 'image/png', file: ostream.toByteArray()]
    }
}
