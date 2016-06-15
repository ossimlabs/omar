package omar.avro
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC
import omar.core.HttpStatus
import java.net.URLConnection
import java.io.BufferedInputStream
import java.io.FileOutputStream

class HttpUtils
{
   static void downloadURI(String destination, String sourceURI)
   {
      URL url = new URL(sourceURI)
      File file = new File(destination)
      if(file.exists())
      {
         file.delete()
      }

      URLConnection connection = url.openConnection();
      connection.setReadTimeout(5000);
      connection.connect();
      // this will be useful so that you can show a typical 0-100% progress bar
      //      int fileLength = connection.contentLength;
      // download the file
      def input = new BufferedInputStream(connection.inputStream);
      def output = new FileOutputStream(file);

      def buffer = new byte[1024];
      long total = 0;
      int count=0;
      while (((count = input.read(buffer)) != -1)) {
         total += count;
      //            publishProgress((int) (total * 100 / fileLength - 1));
         output.write(buffer, 0, count);
      }
   }
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