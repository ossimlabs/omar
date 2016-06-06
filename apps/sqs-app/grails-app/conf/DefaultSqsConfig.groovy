sqs{
   reader{
      enabled = true
      queue = ""
      waitTimeSeconds = 20
      maxNumberOfMessages = 1
      pollingIntervalSeconds = 10
      destination{
         type: "stdout"

//         http{
//            url = ""
//            field = "message"
//         }   
      }
   }
}