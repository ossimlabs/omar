package ossimtools.app

import joms.oms.OssimTools
import geoscript.geom.Point
import geoscript.proj.Projection

class OssimToolsService
{
  static transactional = false

  def grailsApplication

  def calculateExtent(def lat, def lon, def radius)
  {
    def point1 = new Point( lon, lat )
    def point2 = Projection.transform( point1, 'epsg:4326', 'epsg:3857' )
    def bounds = /*Projection.transform(*/ point2.buffer( radius )/*, 'epsg:3857', 'epsg:4326' )*/.bounds


    [[bounds.minX, bounds.minY], [bounds.maxX, bounds.maxY]]
  }

  def execTool(def name, def params)
  {
     def ossimTool = new OssimTools(name);
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
