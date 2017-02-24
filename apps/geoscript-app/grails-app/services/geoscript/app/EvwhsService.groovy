package geoscript.app

import grails.transaction.Transactional
import geoscript.geom.Bounds
import geoscript.layer.WMS
import geoscript.layer.WMSLayer
import geoscript.proj.Projection
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
      def version = params.find { it.key.equalsIgnoreCase('VERSION')}?.value
      def layer = new WMSLayer( wms, ['DigitalGlobe:Imagery'] )
      def bbox = params?.find { it.key.equalsIgnoreCase('BBOX')}?.value?.split(',')?.collect { it?.toDouble() }
      def width = params?.find { it.key.equalsIgnoreCase('WIDTH')}?.value?.toInteger()
      def height = params?.find { it.key.equalsIgnoreCase('HEIGHT')}?.value?.toInteger()
      def srs = params.find { it.key.equalsIgnoreCase('CRS')}?.value ?: params.find { it.key.equalsIgnoreCase('SRS')}?.value
      def ostream = new ByteArrayOutputStream()

      // println ([bbox, width, height, srs])

      def bounds

      if ( version == '1.3.0')
      {
          bounds = new Bounds(bbox[1], bbox[0], bbox[3], bbox[2], srs)
      }
      else
      {
        bounds = new Bounds(bbox[0], bbox[1], bbox[2], bbox[3], srs)
      }

      if ( srs.equalsIgnoreCase('epsg:3857'))
      {
        def map = new GeoScriptMap(
            fixAspectRatio: false,
            width: width,
            height: height,
            bounds: bounds,
            proj: bounds.proj,
            layers: [layer]
        )

        map.render(ostream)
        map.close()
      }
      else
      {
        def bbox3857 = bounds.reproject('epsg:3857')

        // println "foo: ${bbox3857} ${bbox3857.reproject(new Projection('epsg:4326'))}"

        def raster = wms.getRaster([
          width: width,
          height: height,
          bounds: bbox3857,
          srs: bbox3857.proj,
        ], ['DigitalGlobe:Imagery'])

      // println "bar: ${raster.bounds} ${raster.bounds.reproject(new Projection('epsg:4326'))}"

        def map = new GeoScriptMap(
            fixAspectRatio: false,
            width: width,
            height: height,
            bounds: bounds,
            proj: bounds.proj,
            layers: [raster]
        )

        map.render(ostream)
        map.close()
      }

      // def image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
      // ImageIO.write(image, 'png', ostream)

      [contentType: 'image/png', file: ostream.toByteArray()]
    }
}
