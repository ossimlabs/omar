package omar.superoverlay

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Holders

@Slf4j
class OmarSuperOverlayReflectionUtils
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getSuperOverlayConfig() {
      def config = getApplication().config

      config.omar.superOverlay
   }
   static void setSuperOverlayConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.superOverlay = c
   }
}