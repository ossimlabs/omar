package omar.wmts

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class WmtsCommand implements Validateable
{
   String service// = "WMTS"
   String version// = "1.0.0"
   String request// = "GetTile"

   static constraints = {
      service( nullable: true )
      version( nullable: true )
      request( nullable: true )
   }
}
