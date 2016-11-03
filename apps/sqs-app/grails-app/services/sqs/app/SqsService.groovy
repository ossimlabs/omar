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
//import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest
import org.apache.commons.codec.digest.DigestUtils
//import groovyx.net.http.HTTPBuilder
//import groovyx.net.http.ContentType
//import groovyx.net.http.Method
//import groovyx.net.http.RESTClient
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSCredentialsProvider;
//import com.amazonaws.auth.AWSCredentialsProviderChain;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
//import com.amazonaws.auth.InstanceProfileCredentialsProvider;
//import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
//import groovy.json.JsonSlurper

//import grails.transaction.Transactional

//@Transactional
class SqsService {

   AmazonSQSClient sqs
   static Boolean checkMd5(String messageBodyMd5, String message)
   {
      String md5Check =  DigestUtils.md5Hex (message);

      md5Check == messageBodyMd5

   }

   private AWSCredentials createCredentials()
   {
      AWSCredentials credentials = null;
      try {
         credentials= new DefaultAWSCredentialsProviderChain().credentials
      } catch (Exception e) {
         throw new AmazonClientException(
                 "Cannot load the credentials from the DefaultAWSCredentialsProviderChain. ",
                 e);
      }

      credentials
   }

   synchronized def getSqs()
   {
      if(!sqs) sqs = new AmazonSQSClient()//createCredentials())
      sqs
   }
   def postMessage(String url, String message)
   {
      def result = [status:200,message:""]
      try{
         HttpPost post = new HttpPost(url);
         post.addHeader("Content-Type", "application/json");
         StringEntity entity = new StringEntity(message);
         post.setEntity(entity);
         HttpClient client = new DefaultHttpClient();

         HttpResponse response = client.execute(post);

         if(response)
         {
            result.message = response?.statusLine
            result.status = response.statusLine?.statusCode
         }
      }
      catch(e)
      {
         log.debug "${e}"
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
      log.trace "receiveMessages: Entered........"
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
      log.trace "receiveMessages: Leaving........"

      messages
   }
}
