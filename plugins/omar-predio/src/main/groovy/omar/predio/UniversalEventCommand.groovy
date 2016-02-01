package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 1/28/16.
 */
@ToString(includeNames = true)
class UniversalEventCommand implements Validateable
{
   String appName="omar_universal"
   /**
    * Can take on values
    * view, purchase, category-preference, $set
    */
   String event
   String entityType
   String entityId
   String targetEntityType
   String targetEntityId

   /**
    * This should be a HashMap and is typically used in a set (event="$set") event
    * An example set could be
    *     [
    "          category": ["US", "2CMV"],
    "          expireDate": "2016-10-05T21:02:49.228Z"
    "     ]
    */
   def properties

   /**
    * ISO date like yyyy-MM-ddThh:mm:ss.sssZ
    */
   String eventTime

   static constraints = {
      appName blank: false, nullable:false
      event blank:false, nullable:false
      entityType blank:false, nullable:true
      entityId blank:false, nullable:true
      targetEntityType nullable:true
      targetEntityId blank:false, nullable:true
      properties nullable:true
      eventTime nullable:true
   }
}
