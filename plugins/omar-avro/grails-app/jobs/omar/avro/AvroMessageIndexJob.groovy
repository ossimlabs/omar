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
      while(messageRecord = avroService.nextMessage())
      {
        log.info "Processing Message with ID: ${messageRecord.messageId}"
        def slurper = new groovy.json.JsonSlurper()
        try {
          def jsonObj = slurper.parseText(messageRecord.message)
          String suffix = AvroMessageUtils.getDestinationSuffixFromMessage(jsonObj)
          String sourceURI = jsonObj?."${OmarAvroUtils.avroConfig.sourceUriField}"?:""
          if(sourceURI)
          {
            String prefixPath = "${OmarAvroUtils.avroConfig.download.directory}"

            File fullPathLocation = new File(prefixPath, suffix)

            //println "DESTINATION ==== ${fullPathLocation}"
            File testPath = fullPathLocation.parentFile
            if(AvroMessageUtils.tryToCreateDirectory(testPath, [:]))
            {
              URL url = new URL(sourceURI)
              //println "DOWNLOADING: ${jsonObj.S3_URI_Nitf}"
              File file = new File("${fullPathLocation}") 
              file << url.openStream()
              log.info "DOWNLOADED: ${sourceURI} to ${fullPathLocation}"

              avroService.addFile(new IndexFileCommand(filename:fullPathLocation))

              messageRecord = null
            }
            else
            {
              log.error "Unable to create directory ${testPath} for messageId: ${messageRecord.messageId}"
            }
          }
          else
          {
            log.error "Message ${messageRecord?.messageId} not a JSON object or no URI found: ${messageRecord.messageId}"
          }

          // mark the record as null and we do not need to re-index it.
          messageRecord = null
        }
        catch(e) {
          log.error "Error procssing Message: ${messageRecord.messageId}\n${e}"
        }

        if(messageRecord)
        {
          log.error "Re-Indexing the RECORD ${messageRecord.messageId} for it failed to be processed"
          def indexResult = avroService.indexMessage( new IndexMessageCommand(messageRecord))
          if(indexResult.status != 200)
          {
            log.error "Unable to re-index message ${messageRecord.messageId}"
          }
        }
      }
      log.trace "Leaving........."
    }
}
