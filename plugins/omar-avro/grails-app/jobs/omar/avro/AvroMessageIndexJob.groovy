package omar.avro
import groovy.json.JsonSlurper
import omar.core.ProcessStatus

class AvroMessageIndexJob {
   def avroService
  def ingestMetricsService

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
        String messageId = messageRecord.messageId
        ingestMetricsService.startCopy(messageId)

        log.info "Processing Message with ID: ${messageRecord.messageId}"
        try {
          def jsonObj
          try{
            jsonObj = avroService.convertMessageToJsonWithSubField(messageRecord.message)

           // println jsonObj
            // actual image information is in a subfield of the root JSON object
          }
          catch(e)
          {
            avroService.updatePayloadStatus(messageId, ProcessStatus.FAILED, "Unable to parse message.  Not a valid JSON format")
            log.error "Bad Json format.  Message will be ignored!"
          }
          String sourceURI = jsonObj?."${OmarAvroUtils.avroConfig.sourceUriField}"?:""
          if(sourceURI)
          {
            String prefixPath = "${OmarAvroUtils.avroConfig.download.directory}"
            File fullPathLocation = avroService.getFullPathFromMessage(messageRecord.message)
            File testPath = fullPathLocation.parentFile
            HashMap tryToCreateDirectoryConfig = [
                    numberOfAttempts:OmarAvroUtils.avroConfig.createDirectoryRetry.toInteger(),
                    sleepInMillis: OmarAvroUtils.avroConfig.createDirectoryRetryWaitInMillis.toInteger()
            ]

            if(AvroMessageUtils.tryToCreateDirectory(testPath, tryToCreateDirectoryConfig))
            {
              try{
                if(!fullPathLocation.exists())
                {
                  log.info "DOWNLOADING: ${sourceURI} to ${fullPathLocation}"
                  String commandString = OmarAvroUtils.avroConfig.download?.command
                  //println "COMMAND STRING === ${commandString}"
                  if(!commandString)
                  {
                    HttpUtils.downloadURI(fullPathLocation.toString(), sourceURI)
                  }
                  else
                  {
                    HttpUtils.downloadURIShell(commandString, fullPathLocation.toString(), sourceURI)
                  }
                  log.info "DOWNLOADED: ${sourceURI} to ${fullPathLocation}"
                  avroService.updatePayloadStatus(messageId, ProcessStatus.FINISHED, "DOWNLOADED: ${sourceURI} to ${fullPathLocation}")
                }
                else
                {
                  log.info "${fullPathLocation} already exists and will not be re-downloaded"
                  avroService.updatePayloadStatus(messageId, ProcessStatus.FINISHED, "Already exists and will not be downloaded")
                }
                ingestMetricsService.endCopy(messageId)
                avroService.addFile(new IndexFileCommand(filename:fullPathLocation))
                
                messageRecord = null
              }
              catch(e) {
                log.error "Unable to Download: ${sourceURI} to ${fullPathLocation}\nWith error: ${e}"
                if(fullPathLocation?.exists())
                {
                  fullPathLocation?.delete()                  
                }
                avroService.updatePayloadStatus(messageId, ProcessStatus.FAILED, "Unable to Download: ${sourceURI} to ${fullPathLocation} With error: ${e}")
                messageRecord = null
                ingestMetricsService.setStatus(messageId, ProcessStatus.FAILED.toString(), "Unable to Download: ${sourceURI} to ${fullPathLocation} With error: ${e}".toString())
              }
            }
            else
            {
              log.error "Unable to create directory '${testPath}'. "
              avroService.updatePayloadStatus(messageId, ProcessStatus.FAILED, "Unable to create directory '${testPath}'.")
              ingestMetricsService.setStatus(messageId, ProcessStatus.FAILED.toString(),"Unable to create directory '${testPath}'.".toString())
              messageRecord = null
            }
          }
          else
          {
            log.error "JSON is not a proper AVRO message. Field '${OmarAvroUtils.avroConfig.sourceUriField}' not found."
            avroService.updatePayloadStatus(messageId, ProcessStatus.FAILED, "JSON is not a proper AVRO message. Field '${OmarAvroUtils.avroConfig.sourceUriField}' not found.")
            ingestMetricsService.setStatus(messageId, ProcessStatus.FAILED.toString(),"JSON is not a proper AVRO message. Field '${OmarAvroUtils.avroConfig.sourceUriField}' not found.".toString())
            messageRecord = null
          }
        }
        catch(e)
        {
          log.error "${e}"
          avroService.updatePayloadStatus(messageId, ProcessStatus.FAILED, "${messageId} has error: ${e}")
          ingestMetricsService.setStatus(messageId, ProcessStatus.FAILED.toString(),"${messageId} has error: ${e}".toString())
        }
      }

      log.trace "Leaving........."
    }
}