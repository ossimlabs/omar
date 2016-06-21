package omar.avro
import groovy.json.JsonSlurper
import omar.core.ProcessStatus

class AvroFileProcessingJob {
   def avroService
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
            break
          case "post":
            String url   = config.destination.post.addRasterEndPoint
            String field = config.destination.post.addRasterEndPointField
            def result   = HttpUtils.postMessage(url, field, fileRecord.filename)
           
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
              log.error "Post failed to ${url} for  ${fileRecord.filename} with post field ${field}"
              avroService.updateFileStatus(fileRecord.processId, ProcessStatus.FAILED, "Failed to post file to stager")
            }
            break
         }
      }

      log.trace "Leaving........."
    }
}
