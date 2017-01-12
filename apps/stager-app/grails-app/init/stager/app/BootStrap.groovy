package stager.app 
 
import grails.util.Holders
import omar.core.Repository
import omar.stager.StagerJob
import org.quartz.TriggerKey

import grails.util.Environment

class BootStrap {
    def sessionFactory
    def grailsApplication

    def init = { servletContext ->
     if ( Environment.current == Environment.DEVELOPMENT )
      {
        [
          // '/Volumes/Iomega_HDD/data', 
          // '/data/celtic', 
          // '/data1', 
          // '/data/uav'
        ].each {
          println it
          def repo = Repository.findOrCreateByBaseDir( it )
          repo.save()
          StagerJob.triggerNow( baseDir: repo.baseDir )
        }
        sessionFactory?.currentSession?.flush()
      }

       grailsApplication = Holders.grailsApplication
       //StagerConfig.application = grailsApplication
       //StagerUtils.resetSqsConfig()
       //StagerUtils.sqsConfig

       def quartzScheduler = grailsApplication.mainContext.getBean('quartzScheduler')

       //if(SqsUtils.sqsConfig.reader.enabled)
       //{
          org.quartz.TriggerKey triggerKey = new TriggerKey("StageFileJobTrigger", "StageFileJobGroup")

          def trigger = quartzScheduler.getTrigger(triggerKey)
          if(trigger)
          {
             trigger.repeatInterval = 5000l
            // trigger.repeatInterval = StagerUtils.sqsConfig.reader.pollingIntervalSeconds*1000 as Long

             Date nextFireTime=quartzScheduler.rescheduleJob(triggerKey, trigger)
          }
       //}

    }
    def destroy = {
    }
}
