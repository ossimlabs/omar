package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString
import org.joda.time.DateTime

/**
 * Created by gpotts on 1/29/16.
 */
@ToString(includeNames = true)
class ItemRecommendationCommand implements Validateable
{
   String  appName = "omar_universal"
   String  item
   Double  itemBias
   String locations
   Double locationBias
   String categories
   Double categoryBias
   DateTime beforeAvailableDate
   DateTime afterAvailableDate

   Boolean useCurrentDateFilter
   Integer num     = 10

   static constraints = {
      appName blank: false, nullable:false
      item blank:false, nullable:false
      itemBias nullable:true
      locations nullable:true
      locationBias nullable:true
      categories nullable:true
      categoryBias nullable:true
      beforeAvailableDate nullable:true
      afterAvailableDate nullable:true
      useCurrentDateFilter nullable:true
      num blank:false, nullable:false
   }
}
