package omar.avro

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class ResetFileProcessingCommand implements Validateable
{
   String processId
   String status
   String whereStatusEquals

   static contraints = {
      processId nullable: true
      status nullable: false
      whereStatusEquals nullable: true
   }
}
