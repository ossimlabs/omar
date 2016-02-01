import grails.util.Environment
import omar.predio.PredioAppId

/**
 * Created by gpotts on 1/25/16.
 */
class PredioBootStrap
{
   def grailsApplication

   def init = { servletContext ->
      if (Environment.current == Environment.DEVELOPMENT) {
         PredioAppId.withTransaction {
            if (!PredioAppId.findByName("omar_universal"))
            {
               new PredioAppId(name: "omar_universal",
                       eventUrl: "http://predio.local:7070",
                       queryUrl: "http://predio.local:8000",
                       accessKey: "")
                       .save(flush: true)
            }
         }
      }
      else if (Environment.current == Environment.TEST) {
      }
      else if (Environment.current == Environment.PRODUCTION) {
         if (!PredioAppId.findByName("omar_universal"))
         {
            new PredioAppId(name: "omar_universal",
                    eventUrl: "http://<ip>:7070",
                    queryUrl: "http://<ip>:8000",
                    accessKey: "")
                    .save(flush: true)
         }
      }
   }
}
