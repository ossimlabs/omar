package omar.oms

import java.awt.Point
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.Raster
import java.awt.image.RenderedImage
import groovy.util.logging.Slf4j

import joms.oms.Chipper

/**
 * Created by sbortman on 1/15/16.
 */
@Slf4j
class ChipperUtil
{
  static ColorModel createColorModel(int numBands, boolean transparent)
  {
    def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )
    def mask = ( ( 0..<numBands ).collect { 8 } ) as int[]

    def colorModel = new ComponentColorModel( cs, mask,
        transparent, false, ( transparent ) ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
        DataBuffer.TYPE_BYTE )

    return colorModel
  }

  static RenderedImage createImage(Map<String,String> opts, Map<String,Object> hints)
  {
    log.trace "createImage: Entered................"
    def numBands = hints.transparent ? 4 : 3

    def raster = Raster.createInterleavedRaster(
        DataBuffer.TYPE_BYTE,
        hints.width, hints.height,
        hints.width * numBands, numBands, (0..<numBands) as int[],
        new Point( 0, 0 ) )

    runChipper(opts, hints, raster.dataBuffer.data)

    def colorModel = createColorModel(numBands, hints.transparent)
    def image = new BufferedImage(colorModel, raster, false, null)

    log.trace "createImage: Leaving.............."

    return image
  }

  static void runChipper(Map<String,String> opts, Map<String,Object> hints, byte[] buffer)
  {
    log.trace "runChipper: Entered.................."
    def chipper = new Chipper()

    log.trace "runChipper options: ${opts}"
    if ( chipper.initialize( opts ) )
    {
      //println 'initialize: good'
      if ( chipper.getChip( buffer, hints.transparent ) > 1 )
      {
        //println 'getChip: good'
      }
      else
      {
        // println 'getChip: bad'
      }
    }
    else
    {
      // println 'initialize: bad'
    }

    chipper.delete()

    log.trace "runChipper: Leaving.................."
  }
  static Boolean executeChipper(Map<String,String> opts)
  {
    Boolean result = false
    log.trace "runChipper: Entered.................."
    def chipper = new Chipper()

    log.trace "runChipper options: ${opts}"
    if ( chipper.initialize( opts ) )
    {
      result = chipper.execute()
    }
    else
    {
      // println 'initialize: bad'
    }

    chipper.delete()

    log.trace "runChipper: Leaving.................."

    result
  }
}