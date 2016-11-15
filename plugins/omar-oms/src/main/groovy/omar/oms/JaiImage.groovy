package omar.oms

import javax.imageio.ImageIO
import javax.imageio.ImageReadParam
import javax.media.jai.ImageLayout
import javax.media.jai.JAI
import javax.media.jai.ParameterBlockJAI
import javax.media.jai.PlanarImage
import java.awt.Graphics
import java.awt.Image
import java.awt.Point
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.Raster

/**
 * Created by sbortman on 12/7/15.
 */
class JaiImage
{
  static PlanarImage bufferedToPlanar(BufferedImage bi)
  {
    def planarImage = PlanarImage.wrapRenderedImage(bi)
    planarImage = JAI.create("NULL", planarImage)

    planarImage
  }
  static def selectBandsForRendering(PlanarImage planarImage)
  {
    def modifiedImage

    if(planarImage.sampleModel.numBands >=3)
    {
      modifiedImage = JAI.create("BandSelect", planarImage, [0, 1, 2] as int[])
    }
    else
    {
      modifiedImage = JAI.create("BandSelect", planarImage, [0, 0, 0] as int[])
    }

    modifiedImage
  }
  def reformatImage(def image, int tileWidth = 256, int tileHeight = 256)
  {
    def imageLayout = new ImageLayout( image )

    imageLayout.setTileWidth( 256 )
    imageLayout.setTileHeight( 256 )

    def map = [( JAI.KEY_IMAGE_LAYOUT ): imageLayout]
    def hints = new RenderingHints( map )
    def formatParams = new ParameterBlockJAI( 'format' )

    formatParams.setSource( 'source0', image )
    image = JAI.create( 'format', formatParams, hints )
    image
  }

  def readImage(def File file, int rLevel)
  {
    def istream = ImageIO.createImageInputStream( file )
    def reader = ImageIO.getImageReaders( istream )?.next()
    def image

    if ( reader )
    {
      def imageReadParam = new ImageReadParam()

      reader?.input = istream
      image = reader.readAsRenderedImage( rLevel, imageReadParam )
    }

    image
  }

  def readImageInfo(File file)
  {
    def istream = ImageIO.createImageInputStream( file )
    def reader = ImageIO.getImageReaders( istream )?.next()
    def info = [:]

    if ( reader )
    {
      reader?.input = istream
      info.numImages = reader.getNumImages( true )
      info.minIndex = reader.minIndex

      for ( def z in ( info.minIndex..<info.numImages ) )
      {
        def imageReadParam = new ImageReadParam()
        def image = reader.readAsRenderedImage( z, imageReadParam )

        image = reformatImage( image )

        //println image.class.name

        def properties = ['width', 'height', 'tileWidth', 'tileHeight', 'numXTiles', 'numYTiles'].inject( [:] ) {
          a, b -> a."${b}" = image."${b}"; a
        }

        info."image${z}" = properties
      }
    }

    istream?.close()
    return info
  }

  def getTile(GetTileCommand cmd)
  {
    def file = cmd.filename as File
    def imageInfo = readImageInfo( file )
    def index = findIndexOffset( imageInfo ) - ( cmd.z )
    def image = reformatImage( readImage( file, index ) )
    def tileImage = getTileAsImage( image, cmd.x, cmd.y )
    def ostream = new ByteArrayOutputStream()

    ImageIO.write( tileImage, cmd.format, ostream )

    [contentType: "image/${cmd.format}", buffer: ostream.toByteArray()]
  }

  def getTileAsImage(image, x, y)
  {
    def raster = image.getTile( x, y )
    def dataBuffer = raster.getDataBuffer();
    def writableRaster = raster.createWritableRaster( image.sampleModel, dataBuffer, new Point( 0, 0 ) )
    def tileImage = new BufferedImage( image.colorModel, writableRaster, image.colorModel.isAlphaPremultiplied(), null );

    return tileImage
  }


  def findIndexOffset(def imageInfo, def tileSize = 256)
  {
    def index

    for ( def i = imageInfo.minIndex; i < imageInfo.numImages; i++ )
    {
      def levelInfo = imageInfo["image${i}"]

      if ( levelInfo.width <= tileSize && levelInfo.height <= tileSize )
      {
        index = i
        break
      }
    }

    return index
  }

}
