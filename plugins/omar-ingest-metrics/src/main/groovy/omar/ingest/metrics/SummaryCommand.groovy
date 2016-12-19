package omar.ingest.metrics

import grails.validation.Validateable
import groovy.transform.ToString
import org.joda.time.DateTime

@ToString(includeNames = true)
class SummaryCommand implements Validateable
{
   String   ingestId
   /**
    *  summaryType can be OVERALL or INDIVIDUAL
    *  If individual it will return a list of records where it will summarize the time it took for each
    *  stage
    */
   String   summaryType="OVERALL"

   DateTime startDate
   DateTime endDate

   Boolean individual=false

   Integer offset = 0
   Integer limit

   Boolean isNull()
   {
      Boolean result = true

      if(ingestId||startDate||endDate)
      {
         result = false
      }

      result
   }
}