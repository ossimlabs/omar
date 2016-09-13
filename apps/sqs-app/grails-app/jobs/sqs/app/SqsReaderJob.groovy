package sqs.app

class SqsReaderJob {
   def sqsService

   static triggers = {
      simple name: 'SqsReaderTrigger', group: 'SqsReaderGroup'
   }

  def execute() {
    Boolean keepGoing = true
    def messages
    def config = SqsUtils.sqsConfig
    def destinationType = config.reader.destination.type.toLowerCase()
    if(config.reader.queue)
    {
      log.debug "Testing for SQS messages"
      while(messages = sqsService?.receiveMessages())
      {
        log.debug "TRAVERSING MESSAGES"
        def messagesToDelete = []
        def messageBodyList  = []
        messages?.each{message->
          try{
            log.debug "Checking Md5 checksum"
            if(sqsService.checkMd5(message.mD5OfBody, message.body))
            {
              log.debug "PASSED MD5"

              // try a output here and if fails then do not mark the message 
              // for deletion
              //
              switch(destinationType)
              {
                case "stdout":
                  println message.body
                  messagesToDelete << message
                  break
                case "post":
                  String url = config.reader.destination.post.urlEndPoint
                  println "POSTING TO URL ==== ${url}"
                  log.info "Posting message to ${url}"
                  def result = sqsService.postMessage(url, message.body)
                 // is a 200 range response
                 //
                  if((result?.status >= 200) && (result?.status <300))
                  {
                    messagesToDelete << message
                  }
                  else
                  {
                    log.error result?.message?.toString()
                  }
                  break
              }
            }
            else
            {
              log.error("ERROR: BAD MD5 Checksum For Message: ${messageBody}")
              messagesToDelete << message
            }
          }
          catch(e)
          {
            log.error("ERROR: ${e.toString()}")
          }

          messageBodyList = []
        }
        println "MESSAGES DELETING!!!!"
        if(messagesToDelete) sqsService.deleteMessages(
                                       SqsUtils.sqsConfig.reader.queue,
                                       messagesToDelete)
        messagesToDelete = []
      }
    }
  }
}
