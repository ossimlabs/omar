package omar.raster

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class AddRasterCommand implements Validateable
{
   String filename
   Boolean background = false
   Boolean buildOverviews  = false
   Boolean buildHistograms = false
   String overviewCompressionType = "NONE"
   String overviewType = "ossim_tiff_box"

   static contraints = {
      filename nullable: false
      background nullable: false
      buildOverviews nullable: false
      buildHistograms nullable: false
      overviewCompressionType nullable: false
      overviewType nullable: false
   }
}
