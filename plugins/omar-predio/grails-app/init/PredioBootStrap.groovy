import omar.predio.PredioAppId

/**
 * Created by gpotts on 1/25/16.
 */
class PredioBootStrap
{
   def grailsApplication

   def init = { servletContext ->
      PredioAppId.withTransaction {
         if (!PredioAppId.findByName("omar_recommendation"))
         {
            new PredioAppId(name: "omar_recommendation",
                    eventUrl: "http://omar.ossim.org:7070",
                    queryUrl: "http://omar.ossim.org:8000",
                    accessKey: "")
                    .save(flush: true)
         }
         if (!PredioAppId.findByName("omar_trending"))
         {
            new PredioAppId(name: "omar_trending",
                    eventUrl: "http://omar.ossim.org:7070",
                    queryUrl: "http://omar.ossim.org:8001",
                    accessKey: "")
                    .save(flush: true)
         }
      }
   }
}
