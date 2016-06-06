package sqs.app
import grails.core.GrailsApplication
import grails.util.Holders

class SqsConfig
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getSqsConfig() {
      def config = getApplication().config

      config.omar.sqs
   }
   static void setSqsConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.sqs = c
   }
}