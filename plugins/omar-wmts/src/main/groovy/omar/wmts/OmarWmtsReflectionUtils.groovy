package omar.wmts

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Holders

@Slf4j
class OmarWmtsReflectionUtils
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getWmtsConfig() {
      def config = getApplication().config

      config.omar.wmts
   }
   static void setWmtsConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.wmts = c
   }
}