package omar.predio
import grails.util.Environment
import omar.predio.PredioAppId
import groovy.util.logging.Slf4j
import org.quartz.TriggerKey

@Slf4j
class PredioBootStrap
{
   def grailsApplication

   def init = { servletContext ->
      log.trace "init: Entered................................"

      def eventUrl  = OmarPredioUtils.predioConfig.eventUrl?:"http://predio.local:7070"
      def queryUrl  = OmarPredioUtils.predioConfig.queryUrl?:"http://predio.local:8000"
      def accessKey = OmarPredioUtils.predioConfig.accessKey?:""
      def appName   = OmarPredioUtils.predioConfig.appName?:"omar_universal"

      if (Environment.current == Environment.DEVELOPMENT) {
         PredioAppId.withTransaction {
            if (!PredioAppId.findByName("omar_universal"))
            {
               new PredioAppId(name: appName,
                       eventUrl: eventUrl,
                       queryUrl: queryUrl,
                       accessKey: accessKey)
                       .save(flush: true)
            }
         }
      }
      else if (Environment.current == Environment.TEST) {
      }
      else if (Environment.current == Environment.PRODUCTION) {
         if (!PredioAppId.findByName(appName))
         {
            new PredioAppId(name: appName,
                    eventUrl: eventUrl,
                    queryUrl: queryUrl,
                    accessKey: accessKey)
                    .save(flush: true)
         }
      }
      if(OmarPredioUtils.predioConfig.enabled)
      {
         def quartzScheduler = grailsApplication.mainContext.getBean('quartzScheduler')
         org.quartz.TriggerKey triggerKey = new TriggerKey("PredioIndexTrigger", "PredioTriggerGroup")

         def trigger = quartzScheduler.getTrigger(triggerKey)
         if(trigger)
         {
            trigger.repeatInterval = OmarPredioUtils.predioConfig.index.pollingInterval as Long

            Date nextFireTime=quartzScheduler.rescheduleJob(triggerKey, trigger)

         }
      }

      log.trace "init: Leaving................................"

   }
}
