package sqs.app
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest
import org.apache.commons.codec.digest.DigestUtils
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC

import grails.transaction.Transactional

@Transactional
class SqsService {

   AmazonSQSClient sqs
   static Boolean checkMd5(String messageBodyMd5, String message)
   {
     String md5Check =  DigestUtils.md5Hex (message);
        
     md5Check == messageBodyMd5

   }
   synchronized def getSqs()
   {
      if(!sqs) sqs = new AmazonSQSClient()
      sqs
   }
   def postMessage(String url, String field, String message)
   {
      URL tempUrl = new URL(url)
      String host
      if(tempUrl.port> 0)
      {
        host = "${tempUrl.protocol}://${tempUrl.host}:${tempUrl.port}".toString()      
      } 
      else
      {
        host = "${tempUrl.protocol}://${tempUrl.host}".toString()            
      }
      String path = tempUrl.path


      def result = [status:200,message:""]
      try{
        def http = new HTTPBuilder( host )
        def postBody = ["${field}": message] 

        http.post( path: path, body: postBody,
           requestContentType: URLENC ) { resp ->
              result.message = resp.statusLine
              result.status = resp.statusLine.statusCode 
           }
      }
      catch(e)
      {
        result.status = 400
        result.message = e.toString()
      }
      
      result
   }
   def deleteMessages(String queue, def messages)
   {
      def sqs = getSqs()
      def deleteList = []
      Integer entryId = 1
      messages.each{message->
         deleteList << new DeleteMessageBatchRequestEntry(entryId.toString(), message.receiptHandle)
         ++entryId
      }

      if(deleteList)
      {
         sqs.deleteMessageBatch( 
                         new DeleteMessageBatchRequest(queue , 
                               deleteList as List<DeleteMessageBatchRequestEntry>)
                       )
      }
   }
   def receiveMessages() {
      def config = SqsUtils.sqsConfig

      def messages 
      try{
        def sqs = getSqs()
        ReceiveMessageRequest receiveMessageRequest = 
                new ReceiveMessageRequest()
                 .withQueueUrl(config.reader.queue)
                 .withWaitTimeSeconds(config.reader.waitTimeSeconds)
                 .withMaxNumberOfMessages(config.reader.maxNumberOfMessages)
        messages = sqs.receiveMessage(receiveMessageRequest).messages
      }
      catch(e)
      {
         log.error("ERROR: Unable to receive message for queue: ${config.reader.queue}\n${e.toString()}")
      }

      messages
   }
}
