package omar.wmts

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class GetLayersCommand implements Validateable
{
   Integer offset
   Integer limit
   static contraints = {
      offset nullable: true
      limit nullable: true
   }
}
