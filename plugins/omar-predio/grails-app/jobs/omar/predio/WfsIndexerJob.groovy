package omar.predio

import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper
import omar.core.DateUtil
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import omar.predio.SetItemCommand
@Slf4j
class WfsIndexerJob {
   static triggers = {
      simple name: 'PredioIndexTrigger', group: 'PredioTriggerGroup'
   }
   def concurrent = false

   transient predioService
   transient indexJobService

   def execute() {
      log.trace("execute(): Entered ...............")


      def wfsProperties


      while(wfsProperties = indexJobService.nextJob())
      {
         log.trace "WFS Query: ${wfsProperties.toString()}"
         try{

            def config = OmarPredioUtils.predioConfig
            def baseUrl = wfsProperties.wfsUrl
            def itemIdField    = wfsProperties.idField
            def dateField    = wfsProperties.dateField
            def locationFields = wfsProperties.locationFields.split(",")
            def categoryFields = wfsProperties.categoryFields.split(",")
            def expirePeriod = wfsProperties.expirePeriod

            def propertyNames = locationFields + categoryFields + [itemIdField,dateField]

            def wfsAddress = "${baseUrl}&propertyName=${propertyNames.join(',')}&outputFormat=json"
            Integer maxFeatures = config.index.wfs.maxCount

            def fullAddress = wfsAddress+"&startIndex=0&maxFeatures=${maxFeatures}"
            log.trace "Address: ${fullAddress}".toString()
            // log.info "Requesting features from ${fullAddress}"
            def result = new URL(fullAddress).text
            def slurper = new JsonSlurper()
            def jsonObject = slurper.parseText(result)

            Integer idx = maxFeatures
            while(jsonObject?.features)
            {
               jsonObject?.features.each{feature->
                  log.info(feature.toString())
                  def properties = feature.properties
                  def item = properties."${itemIdField}"
                  def categories = []
                  def locations = []
                  def eventTime
                  def expireDate

                  categoryFields.each{field->
                     def fieldValue = properties."${field}"?.toString()?.trim()
                     if(fieldValue) categories << fieldValue
                  }
                  locationFields.each{field->
                     def fieldValue = properties."${field}"?.toString()?.trim()
                     if(fieldValue) locations << fieldValue
                  }
                  if(properties."${dateField}")
                  {
                     if(expirePeriod)
                     {
                        def dateString = properties."${dateField}"
                        def interval = DateUtil.parseOgcTimeIntervalsAsDateTime("""${dateString}/${expirePeriod}""",
                                DateTimeZone.UTC)

                        eventTime = "${interval.start}"
                        expireDate = "${interval.end}"
                     }
                     else
                     {
                        // no expire specified
                        DateTime tempDate = DateUtil.parseDateTime(properties."${dateField}")
                        tempDate = t.toDateTime(DateTimeZone.UTC)
                        eventTime = "${tempDate}"
                     }
                  }
                  try{
                     predioService.setItem(new SetItemCommand(
                             [item:item,
                              categories:categories?.join(","),
                              locations: locations?.join(","),
                              eventTime: eventTime?.toString(),
                              expireDate: expireDate?.toString()
                             ])
                     )
                  }
                  catch(e)
                  {
                     log.error(e.toString())
                  }
               }

               result = new URL(wfsAddress+"&startIndex=${idx}&maxFeatures=${maxFeatures}&formatType").text

               jsonObject = slurper.parseText(result)

               //println jsonObject

               idx += maxFeatures
            }
         }
         catch(e)
         {
            log.error(e.toString())
         }
      }

      log.trace("execute(): Leaving ...............")
   }
}
