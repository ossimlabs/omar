package omar.wmts

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 4/17/15.
 */
@ToString( includeNames = true, includeSuper = true )
class GetTileCommand extends WmtsCommand implements Validateable
{
   String layer
   String format = "image/jpeg"
   String tileMatrixSet = "WorldGeographic"
   Integer tileRow = 0
   Integer tileCol = 0
   Integer tileMatrix = 0

   static contraints = {
      tileMatrixSet nullable: false
      tileMatrix nullable: false
   }
}
