package omar.predio.index

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Holders

@Slf4j
class OmarPredioIndexReflectionUtils
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getPredioIndexConfig() {
      def config = getApplication().config

      config.omar.predio.index
   }
   static void setPredioIndexConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.predio.index = c
   }
}