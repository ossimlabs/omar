package omar.avro

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Holders

@Slf4j
class OmarAvroReflectionUtils
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getAvroConfig() {
      def config = getApplication().config

      config.omar.avro
   }
   static void setAvroConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.avro = c
   }
}