package omar.avro

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class GetFileCommand implements Validateable
{
   Integer offset
   Integer limit
   static contraints = {
      offset nullable: true
      limit nullable: true
   }
}
