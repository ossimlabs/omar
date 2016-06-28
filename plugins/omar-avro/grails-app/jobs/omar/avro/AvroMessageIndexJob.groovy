package omar.avro
import groovy.json.JsonSlurper

class AvroMessageIndexJob {
   def avroService
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def execute() {
      log.trace "Entered........."
      def messageRecord
      Boolean errorFlag = false
      def messageRecordsToRetry = []
      while(messageRecord = avroService.nextMessage())
      {
        log.info "Processing Message with ID: ${messageRecord.messageId}"
        def slurper = new groovy.json.JsonSlurper()
        try {
          def jsonObj
          try{
            jsonObj = slurper.parseText(messageRecord.message)
          } 
          catch(e)
          {
            log.error "Bad Json format.  Message will be ignored!"
          }
          String sourceURI = jsonObj?."${OmarAvroUtils.avroConfig.sourceUriField}"?:""
          if(sourceURI)
          {
            String prefixPath = "${OmarAvroUtils.avroConfig.download.directory}"
            File fullPathLocation = avroService.getFullPathFromMessage(messageRecord.message)
            File testPath = fullPathLocation.parentFile
            if(AvroMessageUtils.tryToCreateDirectory(testPath, [:]))
            {
              try{
                if(!fullPathLocation.exists())
                {
                  log.info "DOWNLOADING: ${sourceURI} to ${fullPathLocation}"
                  HttpUtils.downloadURI(fullPathLocation.toString(), sourceURI)
                  log.info "DOWNLOADED: ${sourceURI} to ${fullPathLocation}"
                }
                else
                {
                  log.info "${fullPathLocation} already exists and will not be re-downloaded"
                }
                avroService.addFile(new IndexFileCommand(filename:fullPathLocation))
                
                messageRecord = null
              }
              catch(e) {
                log.error "Unable to Download: ${sourceURI} to ${fullPathLocation}\nWith error: ${e}"
                if(fullPathLocation?.exists())
                {
                  fullPathLocation?.delete()                  
                }
              }
            }
          }
          else
          {
            log.error "JSON is not a proper AVRO message. Field '${OmarAvroUtils.avroConfig.sourceUriField}' not found."
            messageRecord = null
          }
        }
        catch(e)
        {
          log.error "${e}"            
        }

          // mark the record as null and we do not need to re-index it.

        if(messageRecord)
        {
          messageRecordsToRetry << messageRecord
        }
      }

      messageRecordsToRetry.each{record->
        log.error "Re-Indexing the RECORD ${messageRecord.messageId} for it failed to be processed"
          def indexResult = avroService.addMessage( new IndexMessageCommand(record))
          if(indexResult.status != 200)
          {
            log.error "Unable to re-index message ${messageRecord.messageId}"
          }
      }
      log.trace "Leaving........."
    }
}