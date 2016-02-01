package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 1/29/16.
 */
@ToString(includeNames = true)
class SetItemCommand implements Validateable
{
   String appName = "omar_universal"
   String item
   String categories
   String locations
   String eventTime
   String expireDate
   static constraints = {
      appName    blank: false, nullable: false
      categories blank: false, nullable: true
      locations  blank: false, nullable: true
      eventTime  blank: false, nullable: true
      expireDate blank: false, nullable: true
   }
}
