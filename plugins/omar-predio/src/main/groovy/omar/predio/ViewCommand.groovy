package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 1/28/16.
 */
@ToString(includeNames = true)
class ViewCommand implements Validateable
{
   String appName    = "omar_universal"
   String entityId   = "anonymous"
   String entityType = "user"
   String targetEntityType = "item"
   String targetEntityId
   static constraints = {
      appName blank: false, nullable:true
      entityId blank:false, nullable:false
      entityType nullable:true
      targetEntityType blank:false, nullable:false
      targetEntityId blank:false, nullable:false
   }
}
