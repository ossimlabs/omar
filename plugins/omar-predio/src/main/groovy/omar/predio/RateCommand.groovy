package omar.predio

import groovy.transform.ToString
import grails.validation.Validateable


/**
 * Created by gpotts on 1/20/16.
 */
@ToString
class RateCommand implements Validateable
{
   String appName="omar"
   String event="rate"
   String entityType="user"
   String entityId
   String targetEntityType="item"
   String targetEntityId
   Double rating
   static constraints = {
      appName blank: false, nullable:false
      event blank:false, nullable: false
      entityType blank:false, nullable: false
      entityId blank:false, nullable:false
      targetEntityType blank:false, nullable:false
      targetEntityId blank:false, nullable:false
      rating nullable:true
   }
}
