package sql.app

import grails.util.Holders
import org.quartz.TriggerKey

class BootStrap {
   def grailsApplication
    def init = { servletContext ->
      grailsApplication = Holders.grailsApplication
      SqsConfig.application = grailsApplication
      SqsUtils.resetSqsConfig()
      SqsUtils.sqsConfig

      def quartzScheduler = grailsApplication.mainContext.getBean('quartzScheduler')

      if(SqsUtils.sqsConfig.reader.enabled)
      {
         org.quartz.TriggerKey triggerKey = new TriggerKey("SqsReaderTrigger", "SqsReaderGroup")

         def trigger = quartzScheduler.getTrigger(triggerKey)
         if(trigger)
         {
            trigger.repeatInterval = SqsUtils.sqsConfig.reader.pollingIntervalSeconds*1000 as Long

            Date nextFireTime=quartzScheduler.rescheduleJob(triggerKey, trigger)
         }
      }
    }
    def destroy = {
    }
}
