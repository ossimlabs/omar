package ossimtools.app

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

class OssimToolsService
{
  static transactional = false

  def grailsApplication

  def execTool(def name, def params)
  {
     def ossimTool = new OssimTool(name);
     if (!ossimTool)
         return;
     
     // Using an LUT implies 3 band output. Allocate buffer to receive product:
     int numBands = 3 
     byte[] buffer = new byte[hints.width.toInteger() * hints.height.toInteger() * numBands]
     
     // Eventually need to let the user select colors. For now use hardcoded LUT on server:
     params.lut = grailsApplication?.config?.ossimtools?.supportData?.toString() + "hlz.lut"
             
     if (!ossimTool.initialize(params))
         return;
     
     // Since a new tool is created each time, pass in a bogus map to indicate full AOI should
     // be computed:
     def hints = [ foo : "bar" ]
     if (!ossimTool.getChip(buffer, hints))
         return;
         
     [contentType: '???', buffer: buffer]
  }
  
}
