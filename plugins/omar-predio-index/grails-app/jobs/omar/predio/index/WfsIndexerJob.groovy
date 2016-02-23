package omar.predio.index

import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import omar.core.DateUtil
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

@Slf4j
class WfsIndexerJob {
   static triggers = {
      simple name: 'PredioIndexTrigger', group: 'PredioTriggerGroup'
   }
   def concurrent = false

   transient indexJobService
   def setItem(def item, def locations, def categories, def eventTime = null, def expireDate=null)
   {
      def http = new HTTPBuilder("http://o2.ossim.org")

      http.request(Method.POST, ContentType.TEXT) {
         uri.path = "/o2/predio/setItem"
         uri.query = [item:item, locations:locations?.join(","), categories:categories?.join(",")]
         if(eventTime) uri.query.eventTime   = eventTime.toString()
         if(expireDate) uri.query.expireDate = expireDate.toString()
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


      def wfs = PredioIndexJob.first()


      log.trace wfs?.properties?.toString()
      if(wfs)
      {
         def wfsProperties = wfs.properties
         wfs.delete()
         try{

            def config = OmarPredioIndexUtils.predioIndexConfig
            def baseUrl = wfsProperties.wfsUrl
            def itemIdField    = wfsProperties.idField
            def dateField    = wfsProperties.dateField
            def locationFields = wfsProperties.locationFields.split(",")
            def categoryFields = wfsProperties.categoryFields.split(",")
            def expirePeriod = wfsProperties.expirePeriod


            def propertyNames = locationFields + categoryFields + [itemIdField,dateField]

            def wfsAddress = "${baseUrl}&propertyName=${propertyNames.join(',')}&outputFormat=json"
            Integer maxFeatures = 1000


            def result = new URL(wfsAddress+"&startIndex=0&maxFeatures=${maxFeatures}").text
            def slurper = new JsonSlurper()
            def jsonObject = slurper.parseText(result)

            Integer idx = maxFeatures
            while(jsonObject?.features)
            {

               jsonObject?.features.each{feature->
                  def predioItemParams =  [item:feature."${itemIdField}"]
                  def properties = feature.properties

                  def itemId = properties."${itemIdField}"
                  predioItemParams.categories = []
                  predioItemParams.locations = []

                  categoryFields.each{field->
                     def fieldValue = properties."${field}"?.toString()?.trim()
                     if(fieldValue) predioItemParams.categories << fieldValue
                  }
                  locationFields.each{field->
                     def fieldValue = properties."${field}"?.toString()?.trim()
                     if(fieldValue) predioItemParams.locations << fieldValue
                  }
                  if(properties."${dateField}")
                  {
                     if(expirePeriod)
                     {
                        def dateString = properties."${dateField}"
                        def interval = DateUtil.parseOgcTimeIntervalsAsDateTime("""${dateString}/${expirePeriod}""",
                                                                       DateTimeZone.UTC)

                        predioItemParams.eventTime = "${interval.start}"
                        predioItemParams.expireDate = "${intervale.end}"
                     }
                     else
                     {
                        // no expire specified
                        DateTime tempDate = DateUtil.parseDateTime(properties."${dateField}")
                        tempDate = t.toDateTime(DateTimeZone.UTC)
                        predioItemParams.eventTime = "${tempDate}"
                     }
                  }
                  // setItem(itemId, locations, categories, null)
                 // println "Item id = ${itemId} , locations: ${locations.join(',')}, categories: ${categories.join(',')}"
               }

               result = new URL(wfsAddress+"&startIndex=${idx}&maxFeatures=${maxFeatures}").text
               jsonObject = slurper.parseText(result)

               println jsonObject

               idx += maxFeatures
            }
         }
         catch(e)
         {
            log.error(e.toString())
         }
      }



/*
      try{

      def config = OmarPredioIndexUtils.predioIndexConfig

      if(config?.wfs?.url)
      {
      def baseUrl = "${config.wfs.url}?SERVICE=${config.wfs.service}&VERSION=${config.wfs.version}&typeName=${config.wfs.typeName}"
      def itemIdField    = OmarPredioIndexUtils.predioIndexConfig.idField
      def locationFields = OmarPredioIndexUtils.predioIndexConfig.fields.locations
      def categoryFields = OmarPredioIndexUtils.predioIndexConfig.fields.categories

      def propertyNames = locationFields + categoryFields
      propertyNames << itemIdField
      def wfsAddress = "${baseUrl}&propertyName=${propertyNames.join(',')}"

      println wfsAddress


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
  */
      log.trace("execute(): Leaving ...............")
   }
}
