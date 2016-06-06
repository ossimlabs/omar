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
      while(messages = sqsService?.receiveMessages())
      {
        def messagesToDelete = []
        def messageBodyList  = []
        messages?.each{message->
          try{
            if(sqsService.checkMd5(message.mD5OfBody, message.body))
            {
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
                  String field = config.reader.destination.post.field
                  def result = sqsService.postMessage(url, field, message.body)
                 
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
        if(messagesToDelete) sqsService.deleteMessages(
                                       SqsUtils.sqsConfig.reader.queue,
                                       messagesToDelete)
        messagesToDelete = []
      }
    }
  }
}
