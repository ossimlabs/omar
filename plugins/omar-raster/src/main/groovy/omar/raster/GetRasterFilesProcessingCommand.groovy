package omar.raster

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class GetRasterFilesProcessingCommand implements Validateable
{
   Integer offset
   Integer limit
   String filter
   static contraints = {
      offset nullable: true
      limit nullable: true
   }
}
