package omar.ingest.metrics

import grails.validation.Validateable
import groovy.transform.ToString
import org.joda.time.DateTime

@ToString(includeNames = true)
class IngestCommand implements Validateable
{
   String ingestId

   String newIngestId
   String description

   DateTime startDate
   DateTime endDate

   DateTime startCopy
   DateTime endCopy

   DateTime startStaging
   DateTime endStaging

   String status
   String statusMessage
}