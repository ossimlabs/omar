package omar.oms

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 8/22/16.
 */
@ToString(includeNames = true)
class GrdToIptsCommand implements Validateable
{
   String filename
   Integer entryId = 0
   def pointList

   static constraints = {
      filename blank:false, nullable:false
      entryId nullable:true
      pointList nullable:false
   }
}
