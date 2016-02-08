import omar.core.Repository
import omar.stager.StagerJob

import grails.util.Environment

class BootStrap {
    def sessionFactory

    def init = { servletContext ->
   if ( Environment.current == Environment.DEVELOPMENT )
    {

      ['/Volumes/Iomega_HDD/data', /*'/data/celtic', '/data1', '/data/uav'*/].each {
        println it
        def repo = Repository.findOrCreateByBaseDir( it )
        repo.save()
        StagerJob.triggerNow( baseDir: repo.baseDir )
      }
      sessionFactory?.currentSession?.flush()
    }
    }
    def destroy = {
    }
}
