package omar.jpip

import javax.annotation.PostConstruct
import grails.core.GrailsApplication

/**
 * Created by dburken on 4/22/16.
 */
class Util
{
   // Initialized by grails:
   GrailsApplication grailsApplication

   @PostConstruct
   String getCacheDir()
   {
      String result = new String()
      result = grailsApplication.config.getProperty('jpip.server.cache', "/tmp")
      return result
   }

   @PostConstruct
   String getLogFile()
   {
      String result = new String()
      result = grailsApplication.config.getProperty('jpip.server.log', "/tmp/jpip-log.txt")
      return result
   }
}
