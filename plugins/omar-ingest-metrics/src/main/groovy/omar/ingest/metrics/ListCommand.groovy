package omar.ingest.metrics

import grails.validation.Validateable
import groovy.transform.ToString
import org.joda.time.DateTime

@ToString(includeNames = true)
class ListCommand implements Validateable
{
   String ingestId

   DateTime startDate
   DateTime endDate

   Integer offset = 0
   Integer limit

   String sortBy
}
