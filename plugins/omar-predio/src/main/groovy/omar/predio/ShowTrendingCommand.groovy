package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 1/21/16.
 */
@ToString(includeNames = true)
class ShowTrendingCommand implements Validateable
{
   String appName="omar_trending"
   String entityId="all"
   Integer maxCount = 10
   static constraints = {
      appName nullable:false, blank:false
      entityId nullable:false, blank:false
      maxCount validator:{val ->
         (val&&(val>0))
      }
   }
}