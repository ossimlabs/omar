package omar.avro
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC
import omar.core.HttpStatus

class HttpUtils
{
   static HashMap postMessage(String url, String field, String message)
   {
      def result = [status:200,message:""]
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

      try{
      def http = new HTTPBuilder( host )
      def postBody = ["${field}": message] 
      http.handler.failure = {resp->
        result.status = resp.status
        result.message = resp.statusLine
      }
      http.post( path: path, body: postBody,
         requestContentType: URLENC ) 
      { resp ->
            result.message = resp.statusLine
            result.status = resp.statusLine.statusCode 
       }
      }
      catch(e)
      {
         result.status = HttpStatus.NOT_FOUND
         result.message = e.toString()
      }

      result
   }

}