package omar.avro

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class ResetFileProcessingCommand implements Validateable
{
   String processId
   String status
   String whereStatus

   static contraints = {
      processId nullable: true
      status nullable: false
      whereStatus nullable: true
   }
}
