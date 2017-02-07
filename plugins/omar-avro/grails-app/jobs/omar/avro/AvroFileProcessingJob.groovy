package omar.avro
import groovy.json.JsonSlurper
import omar.core.ProcessStatus

class AvroFileProcessingJob {
   def avroService
   def ingestMetricsService
   static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
   }

   def execute() {
      log.trace "Entered........."

      def fileRecord
      Boolean errorFlag = false
      def config = OmarAvroUtils.avroConfig
      def destinationType = config.destination.type.toLowerCase()
      while(fileRecord = avroService.nextFile())
      {
         switch(destinationType)
         {
            case "stdout":
               println "FINISHED ${fileRecord.filename}"
               avroService.updateFileStatus(fileRecord.processId, ProcessStatus.FINISHED, "File successfully output")
               ingestMetricsService.endIngest(fileRecord.processId)
               break
            case "post":
               sleep( config.stagingDelay ) // ensure that the NFS has enough time to flush bits 
               String url   = config.destination.post.addRasterEndPoint
               String field = config.destination.post.addRasterEndPointField
               HashMap params = config.destination.post.addRasterEndPointParams as HashMap
               params.filename = fileRecord.filename
               println params
               def result   = HttpUtils.postMessage(url, params)

               // is a 200 range response
               //
               if((result?.status >= 200) && (result?.status <300))
               {
                  log.info "Posted Successfully: ${fileRecord.filename} to ${url}"
                  avroService.updateFileStatus(fileRecord.processId, ProcessStatus.FINISHED, "File successfully posted")
               }
               else if(result?.status == 415)
               {
                  log.error "Unable to index FILE: ${fileRecord.filename}.  Typically caused by the file is already present"
                  avroService.updateFileStatus(fileRecord.processId, ProcessStatus.FINISHED, "File recognized but was not added")
               }
               else
               {
                  println result?.status
                  log.error "Post failed to ${url} for  ${fileRecord.filename} with post field ${field}"
                  avroService.updateFileStatus(fileRecord.processId, ProcessStatus.FAILED, "Failed to post file to stager")
                  ingestMetricsService.setStatus(fileRecord.processId, ProcessStatus.FAILED.toString(), "Failed to post file to stager")
               }
               break
         }
      }

      log.trace "Leaving........."
   }
}
