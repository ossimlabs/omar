package omar.predio

import grails.validation.Validateable
import groovy.transform.ToString
import org.joda.time.DateTime

@ToString(includeNames = true)
class PredioIndexDataCommand implements Validateable
{
   String wfsUrl
   String dateRanges
   String locationFields
   String categoryFields
   String dateField
   String idField
   String expirePeriod
   static constraints = {
      wfsUrl          blank: true, nullable: true
      dateRanges      blank: true, nullable: true
      locationFields  blank: true, nullable: true
      categoryFields  blank: true, nullable: true
      dateField       blank: true, nullable: true
      idField         blank: true, nullable: true
      expirePeriod    blank: true, nullable: true
   }
}