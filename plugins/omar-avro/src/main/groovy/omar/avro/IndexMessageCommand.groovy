package omar.avro

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class IndexMessageCommand implements Validateable
{
   String message
   String messageId
   static contraints = {
      message nullable: false
      messageId nullable: true
   }
}
