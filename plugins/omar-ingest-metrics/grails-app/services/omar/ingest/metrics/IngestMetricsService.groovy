package omar.ingest.metrics

import grails.transaction.Transactional
import omar.core.ProcessStatus

@Transactional
class IngestMetricsService {


   HashMap startIngest(String ingestId, String description)
   {
      def ingestMetricsRecord = getIngestMetrics(ingestId)

      if(!ingestMetricsRecord)
      {
         ingestMetricsRecord             = new IngestMetrics()
         ingestMetricsRecord.ingestId    = ingestId
         ingestMetricsRecord.description = description

      }
      else
      {
         ingestMetricsRecord.ingestId     = ingestId
         ingestMetricsRecord.description  = description
         ingestMetricsRecord.startDate    = new Date()
         ingestMetricsRecord.endDate      = null
         ingestMetricsRecord.startCopy    = null
         ingestMetricsRecord.endCopy      = null
         ingestMetricsRecord.startStaging = null
         ingestMetricsRecord.endStaging   = null
         ingestMetricsRecord.status       = ProcessStatus.RUNNING.toString()
      }

      if(!ingestMetricsRecord?.save(flush:true))
      {

      }


   }

   def getIngestMetrics(String ingestId)
   {
      IngestMetrics.findByIngestId(ingestId)
   }
}
