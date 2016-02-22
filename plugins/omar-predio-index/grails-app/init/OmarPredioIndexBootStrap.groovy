package omar.predio.index

import groovy.util.logging.Slf4j
import org.quartz.TriggerKey

@Slf4j
class OmarPredioIndexBootStrap
{
   def grailsApplication

   def init = { servletContext ->
      log.trace "init: Entered................................"
      def quartzScheduler = grailsApplication.mainContext.getBean('quartzScheduler')
      org.quartz.TriggerKey triggerKey = new TriggerKey("PredioIndexTrigger", "PredioTriggerGroup")

      def trigger = quartzScheduler.getTrigger(triggerKey)
      if(trigger)
      {
         trigger.repeatInterval = OmarPredioIndexUtils.predioIndexConfig.pollingInterval as Long
         //trigger.repeatCount=null           // Optional int value, if not set it repeats indefinately.

         Date nextFireTime=quartzScheduler.rescheduleJob(triggerKey, trigger)

      }
      log.trace "init: Leving.................................."
   }
}