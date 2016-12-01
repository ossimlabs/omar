package omar.oms

import groovy.json.JsonSlurper
import omar.core.HttpStatus
import sun.awt.image.ToolkitImage

import javax.media.jai.PlanarImage
import java.awt.Color
import java.awt.Font
import java.awt.Point
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.PixelInterleavedSampleModel
import java.awt.image.Raster
import javax.imageio.ImageIO
import javax.media.jai.JAI

import joms.oms.Chipper
import joms.oms.ImageModel
import joms.oms.Info
import joms.oms.Keywordlist

import java.awt.image.RenderedImage

class ImageSpaceService
{
  static transactional = false

  def getTileOverlay(GetTileCommand cmd)
  {
    //println cmd

    def text = "${cmd.z}/${cmd.x}/${cmd.y}"

    BufferedImage image = new BufferedImage( cmd.tileSize, cmd.tileSize, BufferedImage.TYPE_INT_ARGB )
    ByteArrayOutputStream ostream = new ByteArrayOutputStream()

    def g2d = image.createGraphics()
    def font = new Font( "TimesRoman", Font.PLAIN, 18 )
    def bounds = new TextLayout( text, font, g2d.fontRenderContext ).bounds

    g2d.color = Color.red
    g2d.font = font
    g2d.drawRect( 0, 0, cmd.tileSize, cmd.tileSize )

    // Center Text in tile
    g2d.drawString( text,
        Math.rint( ( cmd.tileSize - bounds.@width ) / 2 ) as int,
        Math.rint( ( cmd.tileSize - bounds.@height ) / 2 ) as int )

    g2d.dispose()

    ImageIO.write( image, cmd.format, ostream )

    [contentType: "image/${cmd.format}", buffer: ostream.toByteArray()]
  }


  def readImageInfo(String file)
  {
    def info = getImageInfoAsMap( file )
    def data = [numImages: info.number_entries as int]

    def images = []

    for ( def i in ( 0..<data.numImages ) )
    {
      def image = info["image${i}"]

      def entry = [
          entry: image.entry as int,
          numResLevels: image.number_decimation_levels as int,
          height: image.number_lines as int,
          width: image.number_samples as int,
      ]

      def resLevels = []

      for ( def l in ( 0..<entry.numResLevels ) )
      {
        resLevels << [
            resLevel: l,
            width: Math.ceil( entry.width / 2**l ) as int,
            height: Math.ceil( entry.height / 2**l ) as int
        ]
      }
      entry.resLevels = resLevels
      images << entry
    }

    data['images'] = images

    return data
  }

  def getImageInfoAsMap(String file)
  {
    def kwl = new Keywordlist()
    def info = new Info()

    info.getImageInfo( file, true, true, true, true, true, true, kwl )

    def data = [:]

    for ( def i = kwl.iterator; !i.end(); )
    {
      //println "${i.key}: ${i.value}"

      def names = i.key.split( '\\.' )
      def prev = data
      def cur = data

      for ( def name in names[0..<-1] )
      {
        if ( !prev.containsKey( name ) )
        {
          prev[name] = [:]
        }

        cur = prev[name]
        prev = cur
      }

      cur[names[-1]] = i.value.trim()
      i.next()
    }

    kwl.delete()
    info.delete()

    return data
  }

  def getTile(GetTileCommand cmd)
  {
      def imageInfo = readImageInfo(cmd.filename)
      def result = [status     : HttpStatus.NOT_FOUND,
                    contentType: "plane/text",
                    buffer     : "Unable to service tile".bytes]
      def imageEntry = imageInfo.images[cmd.entry]
      def indexOffset = findIndexOffset(imageEntry)

      if (cmd.z < imageEntry.numResLevels)
      {
        def rrds = indexOffset - cmd.z
        def opts = [
                cut_bbox_xywh     : [cmd.x * cmd.tileSize, cmd.y * cmd.tileSize, cmd.tileSize, cmd.tileSize].join(','),
                'image0.file'     : cmd.filename,
                'image0.entry'    : cmd.entry as String,
                "operation"       : "chip",
                "scale_2_8_bit"   : "true",
                "rrds"            : "${rrds}".toString(),
                'hist_op'         : cmd.histOp ?: 'auto-minmax',
                'brightness'      : cmd.brightness ? cmd.brightness.toString() : "0.0",
                'contrast'        : cmd.contrast ? cmd.contrast.toString() : "1.0",
                'sharpen_mode'    : cmd.sharpenMode ?: "none",
                "resampler_filter": cmd.resamplerFilter ?: "nearest"
                //three_band_out: "true"
        ]

        if (cmd.bands)
        {
          opts.bands = cmd.bands
        }
        def hints = [
                transparent: cmd.format == 'png',
                width      : cmd.tileSize,
                height     : cmd.tileSize,
                type       : cmd.format,
                ostream    : new ByteArrayOutputStream()
        ]

        //println opts
        def chipperResults = runChipper(opts, hints)
        if (chipperResults.status == HttpStatus.OK)
        {
          result = [status     : HttpStatus.OK,
                    contentType: "image/${hints.type}",
                    buffer     : hints.ostream.toByteArray()]
        } else
        {
          result = chipperResults
        }
      }
    result
  }

  def runChipper(def opts, def hints)
  {
    HashMap result = [status:HttpStatus.NOT_FOUND,
                      contentType: "plane/text",
                      buffer: "Unable to service tile".bytes]


    if(!opts.bands)
    {
      opts.three_band_out="true"
    }
    HashMap chipperResult = ChipperUtil.runChipper(opts)
    if(chipperResult.raster)
    {
      if (chipperResult.raster.numBands > 3)
      {
        try
        {
          def planarImage = JaiImage.bufferedToPlanar(new BufferedImage(chipperResult.colorModel, chipperResult.raster, true, null))
          planarImage.data
          def modifiedImage = JaiImage.selectBandsForRendering(planarImage)
//          if (chipperResult.raster.numBands >= 3)
//          {
//            modifiedImage = JAI.create("BandSelect", planarImage, [0, 1, 2] as int[])
//          } else
//          {
//            modifiedImage = JAI.create("BandSelect", planarImage, [0, 0, 0] as int[])
//          }

          if (modifiedImage)
          {
            chipperResult.raster = modifiedImage.data
            chipperResult.colorModel = modifiedImage.colorModel
          }
//           def planarImage = PlanarImage.wrapRenderedImage(chipperResult.renderedImage)
//           planarImage = JAI.create("NULL", planarImage)
//           planarImage.data

          // def modifedImage = JAI.create("BandSelect", PlanarImage.wrapRenderedImage(chipperResult.renderedImage), [0, 1, 2] as int[])
//            println "MODIFIED IMAGE!!!!!!!!!!!!!${modifedImage}"

        }
        catch (e)
        {
          log.error(e.toString())
        }

      }

      try
      {
      def image = ChipperUtil.optimizeRaster(chipperResult.raster, chipperResult.colorModel, hints)
//hints.type, hints.transparent) //new BufferedImage( chipperResult.colorModel, chipperResult.raster, false, null )
      result.status = HttpStatus.OK
      result.buffer = null
      result.contentType = null

        ImageIO.write(image, hints.type, hints.ostream)

      }
      catch (e)
      {
        log.error(e.toString())
      }
    }
    result
  }


  def findIndexOffset(def image, def tileSize = 256)
  {
    // GP: Currently this will not work correctly because the calling GUI
    // has no way of knowing the R-Levels to use.  It currently assumes that
    // a complete tile fits at the highest resolution but the image does
    // not guarantee that it has overviews beyond that.
    //
    // for now we will always return a full range and will ignore the resolutions
    // predefined by the image
    //
    Integer index = 0;
    Integer maxValue = Math.max(image.width, image.height)

    if((maxValue > 0)&&(tileSize > 0))
    {
      while(maxValue > tileSize)
      {
        maxValue /= 2

        ++index
      }
    }
    /*
    def index

    for ( def i = 0; i < image.numResLevels; i++ )
    {
      def levelInfo = image.resLevels[i]

      if ( levelInfo.width <= tileSize && levelInfo.height <= tileSize )
      {
        index = i
        break
      }
    }
    */
    return index
  }

  def computeUpIsUp(String filename, Integer entryId)
  {
    Double upIsUp = 0.0

    def imageSpaceModel = new ImageModel()
    if ( imageSpaceModel.setModelFromFile( filename, entryId as Integer ) )
    {
      upIsUp = imageSpaceModel.upIsUpRotation();
      imageSpaceModel.destroy()
      imageSpaceModel.delete()
    }

    return upIsUp
  }

  def computeNorthIsUp(String filename, Integer entryId)
  {
    Double northIsUp = 0.0

    def imageSpaceModel = new ImageModel()
    if ( imageSpaceModel.setModelFromFile( filename, entryId as Integer ) )
    {
      northIsUp = imageSpaceModel.northIsUpRotation();
      imageSpaceModel.destroy()
      imageSpaceModel.delete()
    }

    return northIsUp
  }

  def getThumbnail(GetThumbnailCommand cmd)
  {
    def opts = [
        hist_op: 'auto-minmax',
        'image0.file': cmd.filename,
        'image0.entry': cmd.entry as String,
        operation: 'chip',
        output_radiometry: 'U8',
        pad_thumbnail: 'true',
        three_band_out: 'true',
        thumbnail_resolution: cmd.size as String
    ]

    def hints = [
        transparent: cmd.format == 'png',
        width: cmd.size,
        height: cmd.size,
        type: cmd.format,
        ostream: new ByteArrayOutputStream()
    ]

    //println opts
    runChipper( opts, hints )

    [contentType: "image/${hints.type}", buffer: hints.ostream.toByteArray()]
  }

//  def getThumbnail(GetThumbnailCommand cmd)
//  {
//    def output = File.createTempFile( 'chipper', ".${cmd.format}", '/tmp' as File )
//
//    def exe = [
//        "ossim-chipper",
//        "--op",
//        "chip",
//        "--thumbnail",
//        cmd.size,
//        "--entry",
//        cmd.entry,
//        "--pad-thumbnail",
//        "true",
//        "--histogram-op",
//        "auto-minmax",
//        "--output-radiometry",
//        "U8",
//        cmd.filename,
//        output
//    ]
//
//    println exe.join( ' ' )
//
//    def proc = exe.execute()
//
////      proc.consumeProcessOutput(System.out, System.err)
//    proc.consumeProcessOutput()
//    proc.waitFor()
//
//    def buffer = output.bytes
//
//    output.delete()
//
//    [contentType: "image/${cmd.format}", buffer: buffer]
//  }
}
