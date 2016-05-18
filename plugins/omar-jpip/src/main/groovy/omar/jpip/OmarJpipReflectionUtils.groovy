package omar.jpip

import grails.core.GrailsApplication
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j
class OmarJpipReflectionUtils
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getJpipConfig() {
      def config = getApplication().config

      config.omar.jpip
   }
   static void setJpipConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.jpip = c
   }
}
