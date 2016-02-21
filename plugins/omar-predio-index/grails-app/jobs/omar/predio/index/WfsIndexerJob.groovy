package omar.predio.index

import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

@Slf4j
class WfsIndexerJob {
   static triggers = {
      simple name: 'PredioIndexTrigger', group: 'PredioTriggerGroup'
   }
   transient indexJobService
   def setItem(def item, def locations, def categories, def expireDate)
   {
      def http = new HTTPBuilder("http://o2.ossim.org")

      http.request(Method.POST, ContentType.TEXT) {
         uri.path = "/o2/predio/setItem"
         uri.query = [item:item, locations:locations, categories:categories]
         if(expireDate) uri.query.expireDate = expireDate
         //headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

         // response handler for a success response code
         response.success = { resp, reader ->
            println "response status: ${resp.statusLine}"
            println 'Headers: -----------'
            resp.headers.each { h ->
               println " ${h.name} : ${h.value}"
            }

            ret = reader.getText()

            println 'Response data: -----'
            println ret
            println '--------------------'
         }
      }

   }

    def execute() {
       log.trace("execute(): Entered ...............")
       println "Job run! Access to service? ${indexJobService}"
       try{
          def job = indexJobService?.nextJob()
          if(job)
          {
             //def firstObject = PredioIndexJob.first()
             def baseUrl = job.wfsUrl

             def itemIdField    = OmarPredioIndexUtils.predioIndexConfig.idField
             def locationFields = OmarPredioIndexUtils.predioIndexConfig.fields.locations
             def categoryFields = OmarPredioIndexUtils.predioIndexConfig.fields.categories

             def propertyNames = locationFields + categoryFields
             propertyNames << itemIdField
             def wfsAddress = "${baseUrl}&propertyName=${propertyNames.join(',')}"

             println wfsAddress
             Integer maxFeatures = 1000
             def result = new URL(wfsAddress+"&startIndex=0&maxFeatures=${maxFeatures}").text
             def slurper = new JsonSlurper()
             def jsonObject = slurper.parseText(result)

             Integer idx = maxFeatures
             while(jsonObject?.features)
             {

                jsonObject?.features.each{feature->
                   def properties = feature.properties

                   def itemId = properties."${itemIdField}"

                   def categories = []
                   def locations  = []
                   categoryFields.each{field->
                      def fieldValue = properties."${field}"?.toString()?.trim()
                      if(fieldValue) categories << fieldValue
                   }
                   locationFields.each{field->
                      def fieldValue = properties."${field}"?.toString()?.trim()
                      if(fieldValue) locations << fieldValue
                   }
                   // setItem(itemId, locations, categories, null)
                   println "Item id = ${itemId} , locations: ${locations.join(',')}, categories: ${categories.join(',')}"
                }

                result = new URL(wfsAddress+"&startIndex=${idx}&maxFeatures=${maxFeatures}").text
                jsonObject = slurper.parseText(result)
                idx += maxFeatures
             }
          }
       }
       catch(e)
       {
          log.error(e)
       }
       log.trace("execute(): Leaving ...............")
    }
}
