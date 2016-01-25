package hlz.app

import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.layer.GeoTIFF
import geoscript.proj.Projection
import geoscript.render.Map as GeoScriptMap
import org.ossim.oms.util.TransparentFilter

import javax.imageio.ImageIO
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class HlzService
{
  static transactional = false

  def grailsApplication

  def calculateExtent(def lat, def lon, def radius)
  {
    def point1 = new Point( lon, lat )
    def point2 = Projection.transform( point1, 'epsg:4326', 'epsg:3857' )
    def bounds = Projection.transform( point2.buffer( radius ), 'epsg:3857', 'epsg:4326' ).bounds


    [[bounds.minX, bounds.minY], [bounds.maxX, bounds.maxY]]
  }


  def runHLZ(double lat, double lon, double radiusROI, double radiusLZ, /*double roughness,*/ double slope, File outputFile)
  {
    def cmd = [
        "ossim-hlz",
        "--target", lat, lon,
        "--lut", "${grailsApplication.config.hlz.supportData}/hlz.lut",
        "--roi", radiusROI,
        "--rlz", radiusLZ,
        "--threads", 8,
//        "--roughness", roughness,
        "--slope", slope,
        outputFile.absolutePath,
    ]

    print cmd.join( ' ' )

    def proc = cmd.execute()

//    proc.consumeProcessOutput(System.out, System.err)
    proc.consumeProcessOutput()

    println proc.waitFor()
  }

  def runVS(double lat, double lon, double radius, double fovStart, double fovStop, double heightOfEye, File outputFile)
  {
    def cmd = [
        'ossim-viewshed',
        '--radius', radius,
        '--fov', fovStart, fovStop,
        '--hgt-of-eye', heightOfEye,
        "--lut", "${grailsApplication.config.hlz.supportData}/vs.lut",
        lat, lon,
        outputFile.absolutePath
    ]

    print cmd.join( ' ' )

    def proc = cmd.execute()

//    proc.consumeProcessOutput(System.out, System.err)
    proc.consumeProcessOutput()

    println proc.waitFor()
  }

  def renderHLZ(def params)
  {
    def tmpDir = grailsApplication?.config?.hlz?.tmpDir?.toString() as File
    def file = File.createTempFile( 'hlz', '.tif', tmpDir )

    runHLZ(
        params['lat'].toDouble(),
        params['lon'].toDouble(),
        params['radiusROI'].toDouble(),
        params['radiusLZ'].toDouble(),
//        params['roughness'].toDouble(),
        params['slope'].toDouble(),
        file
    )

    def geotiff = new GeoTIFF( file )
    def raster = geotiff.read()
    def ostream = new ByteArrayOutputStream()

//    def info = [
//        name: file.name,
//        bounds: raster.bounds,
//        width: raster.cols,
//        height: raster.rows
//    ]

    def bounds = new Bounds( *( params['BBOX'].split( ',' )*.toDouble() ), params['SRS'] )

    def map = new GeoScriptMap(
        layers: [raster],
        width: params['WIDTH'].toInteger(),
        height: params['HEIGHT'].toInteger(),
        bounds: bounds,
        proj: bounds.proj,
        type: 'png'
    )

    def image = map.renderToImage()

    map?.close()

    file?.delete()
    image = TransparentFilter.fixTransparency( new TransparentFilter(), image )
//    image = makeTranslucent( image, 0.5 )

    ImageIO.write( image, map.type, ostream )
    raster?.dispose()

    [contentType: 'image/png', buffer: ostream.toByteArray()]
  }

  def renderVS(def params)
  {
    def tmpDir = grailsApplication?.config?.hlz?.tmpDir?.toString() as File
    def file = File.createTempFile( 'ovs', '.tif', tmpDir )

    runVS(
        params['lat'].toDouble(),
        params['lon'].toDouble(),
        params['radius'].toDouble(),
        params['fovStart'].toDouble(),
        params['fovStop'].toDouble(),
        params['heightOfEye'].toDouble(),
        file
    )

    def geotiff = new GeoTIFF( file )
    def raster = geotiff.read()
    def ostream = new ByteArrayOutputStream()

//    def info = [
//        name: file.name,
//        bounds: raster.bounds,
//        width: raster.cols,
//        height: raster.rows
//    ]

    def bounds = new Bounds( *( params['BBOX'].split( ',' )*.toDouble() ), params['SRS'] )

    def map = new GeoScriptMap(
        layers: [raster],
        width: params['WIDTH'].toInteger(),
        height: params['HEIGHT'].toInteger(),
        bounds: bounds,
        proj: bounds.proj,
        type: 'png'
    )

    def image = map.renderToImage()

    map?.close()

    file?.delete()
    image = TransparentFilter.fixTransparency( new TransparentFilter(), image )
//    image = makeTranslucent( image, 0.5 )

    ImageIO.write( image, map.type, ostream )
    raster?.dispose()

    [contentType: 'image/png', buffer: ostream.toByteArray()]
  }


  def renderHillShade(def params)
  {
    def file = grailsApplication?.config?.hlz?.hillShade?.toString() as File
    def geotiff = new GeoTIFF( file )
    def raster = geotiff.read()
    def ostream = new ByteArrayOutputStream()
    def bounds = new Bounds( *( params['BBOX'].split( ',' )*.toDouble() ), params['SRS'] )

    def map = new GeoScriptMap(
        layers: [raster],
        width: params['WIDTH'].toInteger(),
        height: params['HEIGHT'].toInteger(),
        bounds: bounds,
        proj: bounds.proj,
        type: 'png'
    )

    map.render( ostream )
    map.close()
    raster?.dispose()

    [contentType: 'image/png', buffer: ostream.toByteArray()]

  }


  private BufferedImage makeTranslucent(BufferedImage source, float alpha)
  {
    Graphics2D g2d = source.createGraphics();
    g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );
    g2d.drawImage( source, 0, 0, null );
    g2d.dispose();
    return source;
  }
}
