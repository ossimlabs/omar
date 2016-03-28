package omar.wmts
import groovy.util.logging.Slf4j


@Slf4j
class OmarWmtsBootStrap
{
   def grailsApplication
   def init = { servletContext ->
      log.trace "init: Entered................................"

      if(!OmarWmtsLayer.list())
      {
         OmarWmtsTileMatrixSet geographicTileMatrixSet = new OmarWmtsTileMatrixSet(
                 name: "WorldGeographic",
                 minX:-180.0,
                 minY:-90.0,
                 maxX:180.0,
                 maxY:90.0,
                 minLevel: 0,
                 maxLevel: 20,
                 tileWidth: 256,
                 tileHeight: 256,
                 epsgCode:"epsg:4326" )
         geographicTileMatrixSet.save(flush:true)

         OmarWmtsLayer worldGeographicLayer = new OmarWmtsLayer(
                                          name:"WorldGeographic",
                                          title:"World Geographic Layer",
                                          description:"World Geographic Layer",
                                          omarWmtsTileMatrixSet:geographicTileMatrixSet
                                       )

         worldGeographicLayer.save(flush:true)

/*         OmarWmtsTileMatrixSet mercatorTileMatrixSet = new OmarWmtsTileMatrixSet(
                 name: "WorldMercator",
                 minX:-20037508.342789244,
                 minY:-20037508.342789244,
                 maxX:20037508.342789244,
                 maxY:20037508.342789244,
                 minLevel: 0,
                 maxLevel: 20,
                 tileWidth: 256,
                 tileHeight: 256,
                 epsgCode:"epsg:3857" )

         mercatorTileMatrixSet.save(flush:true)
         OmarWmtsLayer worldMercatorLayer = new OmarWmtsLayer(
                 name:"WorldMercator",
                 title:"World Mercator Layer",
                 description:"World Mercator Layer",
                 omarWmtsTileMatrixSet:mercatorTileMatrixSet
         )
         worldMercatorLayer.save(flush:true)
*/
      }
      log.trace "init: Leaving................................"
   }
}