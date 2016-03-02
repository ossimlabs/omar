package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString
import org.joda.time.DateTime

/**
 * Created by gpotts on 1/29/16.
 */
@ToString(includeNames = true)
class UserRecommendationCommand  implements Validateable
{
   String  appName = "omar_universal"
   String  user    = "anonymous"
   Double  userBias
   Integer num     = 10

   String locations
   Double locationBias

   String categories
   Double categoryBias

   DateTime beforeAvailableDate
   DateTime afterAvailableDate

   Boolean useCurrentDateFilter
   static constraints = {
      appName blank: false, nullable:true
      user blank:false, nullable:false
      userBias nullable:true
      num blank:false, nullable:false
      locations nullable:true
      locationBias nullable:true
      categories nullable:true
      categoryBias nullable:true
      beforeAvailableDate nullable:true
      afterAvailableDate nullable:true
      useCurrentDateFilter nullable:true
   }
}
