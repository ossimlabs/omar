package omar.wmts

import geoscript.geom.Point
import groovy.transform.ToString

@ToString(includeNames=true)
class TileMatrix
{
   String identifier
   Double scaleDenominator
   Point topLeftCorner
   Integer tileWidth
   Integer tileHeight
   Integer matrixWidth
   Integer matrixHeight
}

