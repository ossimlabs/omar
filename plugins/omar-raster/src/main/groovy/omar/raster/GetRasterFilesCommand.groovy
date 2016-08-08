package omar.raster

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class GetRasterFilesCommand  implements Validateable
{
   String id
}
