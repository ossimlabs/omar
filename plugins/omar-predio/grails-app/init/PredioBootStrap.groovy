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
                    accessKey: "WX7PdbG91dL7siF6Hqif0FgiTG7Romjzp4BEfgZND2Fmn7rPMRRzPS2DHXdGm51j")
                    .save(flush: true)
         }
         if (!PredioAppId.findByName("omar_trending"))
         {
            new PredioAppId(name: "omar_trending",
                    eventUrl: "http://omar.ossim.org:7070",
                    queryUrl: "http://omar.ossim.org:8001",
                    accessKey: "v7uTPPgnlm2tZKjZErDFi4818zSVLxfqM4xlOwPYJkXTFeVZuVCw9MIq5qqUpD95")
                    .save(flush: true)
         }
      }
   }
}
