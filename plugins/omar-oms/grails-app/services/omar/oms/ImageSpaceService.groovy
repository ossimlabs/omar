package omar.oms

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

import joms.oms.Chipper
import joms.oms.ImageModel
import joms.oms.Info
import joms.oms.Keywordlist

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


  def readImageInfo(File file)
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

  def getImageInfoAsMap(File file)
  {
    def kwl = new Keywordlist()
    def info = new Info()

    info.getImageInfo( file.absolutePath, true, true, true, true, true, true, kwl )

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
    def imageInfo = readImageInfo( cmd.filename as File )

    def opts = [
        cut_bbox_xywh: [cmd.x * cmd.tileSize, cmd.y * cmd.tileSize, cmd.tileSize, cmd.tileSize].join( ',' ),
        'image0.file': cmd.filename,
        'image0.entry': cmd.entry as String,
        operation: 'chip',
        rrds: "${findIndexOffset( imageInfo.images[cmd.entry] ) - ( cmd.z )}".toString(),
        scale_2_8_bit: 'true',
        'hist_op': 'auto-minmax',
        three_band_out: "true"
    ]

    def hints = [
        transparent: cmd.format == 'png',
        width: cmd.tileSize,
        height: cmd.tileSize,
        type: cmd.format,
        ostream: new ByteArrayOutputStream()
    ]

    //println opts
    runChipper( opts, hints )

    [contentType: "image/${hints.type}", buffer: hints.ostream.toByteArray()]
  }

  def runChipper(def opts, def hints)
  {
    def chipper = new Chipper()
    def numBands = ( hints.transparent ) ? 4 : 3
    def buffer = new byte[hints.width * hints.height * numBands]

//    println buffer.size()

    if ( chipper.initialize( opts ) )
    {
      if ( chipper.getChip( buffer, hints.transparent ) > 1 )
      {
//        println 'getChip: good'
      }
      else
      {
        println "getChip: bad ${opts}"
      }
    }
    else
    {
      println "initialize: bad ${opts}"
    }
    chipper?.delete()

    def dataBuffer = new DataBufferByte( buffer, buffer.size() )

    def sampleModel = new PixelInterleavedSampleModel(
        DataBuffer.TYPE_BYTE,
        hints.width,                      // width
        hints.height,                     // height
        numBands,                         // pixelStride
        hints.width * numBands,           // scanlineStride
        ( 0..<numBands ) as int[]         // band offsets
    )

    def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )
    def mask = ( ( 0..<sampleModel.numBands ).collect { 8 } ) as int[]

    def colorModel = new ComponentColorModel( cs, mask,
        hints.transparent, false, ( hints.transparent ) ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
        DataBuffer.TYPE_BYTE )

    def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )
    def image = new BufferedImage( colorModel, raster, false, null )

    ImageIO.write( image, hints.type, hints.ostream )
  }


  def findIndexOffset(def image, def tileSize = 256)
  {
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
