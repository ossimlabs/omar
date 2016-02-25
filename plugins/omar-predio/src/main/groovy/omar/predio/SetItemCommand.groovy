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
      categories blank: true, nullable: true
      locations  blank: true, nullable: true
      eventTime  blank: true, nullable: true
      expireDate blank: true, nullable: true
   }
}
