package omar.oms

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Font
import java.awt.font.TextLayout
import java.awt.image.BufferedImage

class ImageSpaceService
{
  static transactional = false
  def imageUtil = new OmsImage()

  def getTileOverlay(GetTileCommand cmd)
  {
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


  def getTile(GetTileCommand cmd)
  {
    //new JaiImage().getTile( cmd )
    imageUtil.getTile( cmd )
  }

  synchronized def readImageInfo(File file)
  {
    imageUtil.readImageInfo( file )
  }

  def findIndexOffset(def image, def tileSize = 256)
  {
    imageUtil.findIndexOffset( image, tileSize )
  }
}
