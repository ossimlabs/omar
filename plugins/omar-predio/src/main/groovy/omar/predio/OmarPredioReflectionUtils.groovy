package omar.predio

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Holders

@Slf4j
class OmarPredioReflectionUtils
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getPredioConfig() {
      def config = getApplication().config

      config.omar.predio
   }
   static void setPredioConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.predio = c
   }
}