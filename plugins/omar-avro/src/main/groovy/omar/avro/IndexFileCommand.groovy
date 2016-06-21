package omar.avro

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class IndexFileCommand implements Validateable
{
   String processId
   String filename
   String status = "READY"
   String statusMessage = ""

   static contraints = {
      processId nullable: true
      filename nullable: false
      status nullable: false
      statusMessage nullable: true
   }
}
