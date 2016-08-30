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
   def pointList
   Boolean pqeIncludePositionError = false
   String pqeEllipsePointType = "none" // can be "none" or "array", "polygon", "linestring"
   Double pqeProbabilityLevel = 0.9
   Double pqeEllipseAngularIncrement = 10

   static constraints = {
      filename blank:false, nullable:false
      entryId nullable:true
      pointList nullable:false
      pqeIncludePositionError nullable:true, validator: {value, object -> }
      pqeEllipsePointType nullable:true , validator: {value, object -> }
      pqeProbabilityLevel nullable:true , validator: {value, object -> }
      pqeEllipseAngularIncrement nullable:true , validator: {value, object -> }
   }
}
