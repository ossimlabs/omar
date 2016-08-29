package omar.oms

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 8/22/16.
 */
@ToString(includeNames = true)
class IptsToGrdCommand implements Validateable
{
   String filename
   Integer entryId = 0
   ArrayList ipts
   def pqe = [
           includePositionError:false,
           ellipsePointType:"none", // can be "none" or "array", future will allow for WKT types
           probabilityLevel:0.9
   ]
//   Boolean includePositionError = false
//   Boolean includeEllipsePoints = false
//   Double probabilityLevel = 0.90

   static constraints = {
      filename blank:false, nullable:false
      entryId nullable:true
      ipts nullable:false
      pqe nullable:true
//      includePositionError nullable:true
//      includeEllipsePoints nullable:true
//      probabilityLevel nullable:true
   }
}
