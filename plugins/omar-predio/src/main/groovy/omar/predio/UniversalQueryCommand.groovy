package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 1/28/16.
 */
@ToString(includeNames = true)
class UniversalQueryCommand implements Validateable
{
   String appName="omar_universal"
   /**
    * Get recommendations given an item
    */
   String item

   /**
    * Get recomendations for a given user
    */
   String user

   /**
    * These are specific event names such a "view" or "buy",  This is a comma separated
    * list of events.  So if you want view recommendations then your event names would just
    * be view
    */
   def eventNames = []

   /**
    * Fields can have the following syntax and can be a comma separated list of name sets.
    *
    *
    * "fields": [{
    *     "name": "categories",
    *     "values": ["<comma separated list of categories>"],
    *     "bias": <numeric bias for the categories>
    * }]
    */
   def fields = []

   def dateRange

   Integer num = 10

   static constraints = {
      appName nullable:false, blank:false
      item nullable:true
      user nullable:true
      eventNames nullable:true
      fields nullable:true
      dateRange nullable:true
      num validator:{val ->
         ((val!=null)&&(val>0))
      }
   }

}
