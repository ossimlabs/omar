package omar.ingest.metrics

import grails.validation.Validateable
import groovy.transform.ToString
import org.joda.time.DateTime

@ToString(includeNames = true)
class DeleteCommand implements Validateable
{
   String ingestId
   DateTime startDate
   DateTime endDate

}