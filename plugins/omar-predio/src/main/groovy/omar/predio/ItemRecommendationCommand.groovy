package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 1/29/16.
 */
@ToString(includeNames = true)
class ItemRecommendationCommand implements Validateable
{
   String  appName = "omar_universal"
   String  item
   Integer num     = 10

   static constraints = {
      appName blank: false, nullable:false
      item blank:false, nullable:false
      num blank:false, nullable:false
   }
}
