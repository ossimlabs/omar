package omar.wmts

import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.layer.Grid
import geoscript.layer.Pyramid

/**
 * Created by sbortman on 4/17/15.
 */
class TileMatrixSet
{
   String identifier
   Bounds bounds
   Integer minLevel
   Integer maxLevel
   Integer tileWidth
   Integer tileHeight
   Pyramid pyramid
   List<TileMatrix> tileMatrices = []

   TileMatrixSet(HashMap params)
   {
      bounds     = params.bounds
      minLevel   = params.minLevel
      maxLevel   = params.maxLevel
      identifier = params.identifier?:"${bounds.proj.id}"
      tileWidth  = params.tileWidth?:256
      tileHeight = params.tileHeight?:256

//      println "TileMatrixSet BOUNDS: ${bounds}"
      pyramid = createPyramid()

      Double f1 = (bounds.proj.epsg == 4326) ? Math.toRadians(bounds.width) * 6378137 : bounds.width
      Double f2 = f1 / pyramid.tileWidth / 0.00028


      for ( Integer z in (0..maxLevel) )
      {
         def grid = pyramid.grid(z)

         tileMatrices << new TileMatrix(
                 identifier: "${z}",
                 scaleDenominator: f2 / grid.width,
                 topLeftCorner: new Point(bounds.minX, bounds.maxY),
                 tileWidth: pyramid.tileWidth,
                 tileHeight: pyramid.tileHeight,
                 matrixWidth: grid.width,
                 matrixHeight: grid.height
         )
      }
   }
   Pyramid createPyramid()
   {
      def pyramid = new Pyramid(tileWidth: tileWidth, tileHeight:tileHeight,
                                bounds: bounds, proj: bounds.proj,
                                origin: Pyramid.Origin.TOP_LEFT)
      def zeroRes = bounds.width / pyramid.tileWidth
      def numberTilesAtRes0 = (bounds.proj.epsg == 4326) ? 2 : 1


      pyramid.grids = (0..maxLevel).collect { z ->
         def n = ( 2 ** z )
         def res = zeroRes / n
         //println "${z} ${res}"
         //println "${res}"
         new Grid(z,numberTilesAtRes0*n,n,res/pyramid.tileWidth,res/pyramid.tileWidth)
      }
      pyramid
   }
}
