package omar.predio
import io.prediction.Event;
import io.prediction.EventClient
import io.prediction.EngineClient
import grails.transaction.Transactional
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.springframework.http.HttpStatus
import com.google.gson.JsonObject

@Transactional
class PredioService
{

   def sendUniversalEvent(UniversalEventCommand cmd)
   {
      HashMap result = [status     : HttpStatus.OK,
                        message    : "Success",
                        contentType: "text/plain"
      ]
      if(!OmarPredioUtils.predioConfig.enabled)
      {
         result.status  = HttpStatus.METHOD_NOT_ALLOWED
         result.message = "PredictionIO is currently disabled"

         return result
      }
      if (cmd.validate())
      {
         try
         {
            PredioAppId appId = PredioAppId.findByName(cmd.appName)
            EventClient client = new EventClient(appId.accessKey, appId.eventUrl)
            if (appId)
            {
               Event viewEvent = new Event()
               if (cmd.event) viewEvent = viewEvent.event(cmd.event)
               if (cmd.entityType) viewEvent = viewEvent.entityType(cmd.entityType)
               if (cmd.entityId) viewEvent = viewEvent.entityId(cmd.entityId)
               if (cmd.targetEntityType) viewEvent = viewEvent.targetEntityType(cmd.targetEntityType)
               if (cmd.targetEntityId) viewEvent = viewEvent.targetEntityId(cmd.targetEntityId)
               if (cmd.properties) viewEvent = viewEvent.properties(cmd.properties)
               if (cmd.eventTime) viewEvent = viewEvent.eventTime(cmd.eventTime)
               client.createEvent(viewEvent)
            }
            else
            {
               result.status = HttpStatus.BAD_REQUEST
               result.message = "PredictiveIO App Instance not found for appName ${cmd.appName}"
            }
         }
         catch (e)
         {
            result.status = HttpStatus.BAD_REQUEST
            result.message = e.message
         }
      } else
      {
         def messages = []
         String message = "Invalid parameters"
         //cmd.allErrors.each{messages << "${it}"}
         //message = messages.join("\n").toString()
         result = [status : HttpStatus.BAD_REQUEST,
                   message: message.toString()]
      }

      result

   }
   def setItem(SetItemCommand cmd)
   {
      UniversalEventCommand universalCmd = new UniversalEventCommand()
      universalCmd.event = "\$set"
      universalCmd.entityType = "item"
      universalCmd.entityId = cmd.item

      universalCmd.targetEntityId = null
      universalCmd.targetEntityType = null

      def properties = [:]
      if(!OmarPredioUtils.predioConfig.enabled)
      {
         result.status  = HttpStatus.METHOD_NOT_ALLOWED
         result.message = "PredictionIO is currently disabled"

         return result
      }
      if(cmd.categories)
      {
         properties.categories = []
         cmd.categories?.split(",")?.each{String category->
            String tempString = category.trim()
            if(tempString)
            {
               properties.categories << tempString
            }
         }
      }
      if(cmd.locations)
      {
         properties.locations = []
         cmd.locations?.split(",")?.each{String location->
            String tempString = location.trim()
            if(tempString) properties.locations << tempString
         }
      }
      if(cmd.expireDate) properties.expireDate = cmd.expireDate
      if(cmd.eventTime) universalCmd.eventTime = cmd.eventTime
      universalCmd.properties = properties?:null


      sendUniversalEvent(universalCmd)
   }

   def getUserRecommendations(UserRecommendationCommand cmd)
   {
      // setup universal command and call the service
      UniversalQueryCommand universalCmd = new UniversalQueryCommand()
      universalCmd.user     = cmd.user
      universalCmd.userBias = cmd.userBias
      universalCmd.appName  = cmd.appName
      universalCmd.num      = cmd.num

      def locations = []
      def categories = []

      def fields = []

      cmd.locations?.split(",").each{location->
         locations << location.trim()
      }
      cmd.categories?.split(",").each{category->
         categories << category.trim()
      }
      if(!OmarPredioUtils.predioConfig.enabled)
      {
         result.status  = HttpStatus.METHOD_NOT_ALLOWED
         result.message = "PredictionIO is currently disabled"

         return result
      }
      if(locations)
      {
         HashMap mapping = [name:"locations",
                            values:locations]
         if(cmd.locationBias!=null)
         {
            mapping.bias = cmd.locationBias
         }
         fields << mapping
      }
      if(categories)
      {
         HashMap mapping = [name:"categories",
                            values:categories]
         if(cmd.categoryBias!=null)
         {
            mapping.bias = cmd.categoryBias
         }
         fields << mapping
      }
      if(cmd.useCurrentDateFilter)
      {
         universalCmd."currentDate" = new DateTime().withZone(DateTimeZone.UTC);
      }
      else if(cmd.beforeAvailableDate||cmd.afterAvailableDate)
      {
         universalCmd.dateRange = [name:"availableDate"]
         if(cmd.afterAvailableDate)
         {
            universalCmd.dateRange.after =  cmd.afterAvailableDate.toString()
         }
         if(cmd.beforeAvailableDate)
         {
            universalCmd.dateRange.before =  cmd.beforeAvailableDate.toString()
         }
      }
      if(fields) universalCmd.fields = fields


      getRecommendations(universalCmd)
   }
   def getItemRecommendations(ItemRecommendationCommand cmd)
   {
      // setup universal command and call the service
      UniversalQueryCommand universalCmd = new UniversalQueryCommand()
      universalCmd.item     = cmd.item
      universalCmd.itemBias = cmd.itemBias
      universalCmd.appName  = cmd.appName
      universalCmd.num      = cmd.num

      def locations = []
      def categories = []

      def fields = []

      if(!OmarPredioUtils.predioConfig.enabled)
      {
         result.status  = HttpStatus.METHOD_NOT_ALLOWED
         result.message = "PredictionIO is currently disabled"

         return result
      }
      cmd.locations?.split(",").each{location->
         locations << location.trim()
      }
      cmd.categories?.split(",").each{category->
         categories << category.trim()
      }
      if(locations)
      {
         HashMap mapping = [name:"locations",
                            values:locations]
         if(cmd.locationBias!=null)
         {
            mapping.bias = cmd.locationBias
         }
         fields << mapping
      }
      if(categories)
      {
         HashMap mapping = [name:"categories",
                            values:categories]
         if(cmd.categoryBias!=null)
         {
            mapping.bias = cmd.categoryBias
         }
         fields << mapping
      }
      if(cmd.useCurrentDateFilter)
      {
         universalCmd."currentDate" = new DateTime().withZone(DateTimeZone.UTC);
      }
      else if(cmd.beforeAvailableDate||cmd.afterAvailableDate)
      {
         universalCmd.dateRange = [name:"availableDate"]
         if(cmd.afterAvailableDate)
         {
            universalCmd.dateRange.after =  cmd.afterAvailableDate.toString()
         }
         if(cmd.beforeAvailableDate)
         {
            universalCmd.dateRange.before =  cmd.beforeAvailableDate.toString()
         }
      }
      if(fields) universalCmd.fields = fields

      getRecommendations(universalCmd)
   }


   def getRecommendations(UniversalQueryCommand cmd)
   {
      HashMap result = [status     : HttpStatus.OK,
                        message    : [],
                        contentType: "text/plain"]

      if(!OmarPredioUtils.predioConfig.enabled)
      {
         result.status  = HttpStatus.METHOD_NOT_ALLOWED
         result.message = "PredictionIO is currently disabled"

         return result
      }
      if (cmd.validate())
      {
         PredioAppId appId = PredioAppId.findByName(cmd.appName)
         if (appId)
         {
            if (appId.accessKey)
            {
               def temp = "${appId.queryUrl}?/"
               EngineClient engine = new EngineClient(appId.queryUrl)
               HashMap queryParams = [:]

               if (cmd.fields) queryParams.fields = cmd.fields
               if (cmd.dateRange) queryParams.dateRange = cmd.dateRange
               if (cmd.currentDate) queryParams.currentDate = cmd.currentDate
               if (cmd.eventNames) queryParams.eventNames = cmd.eventNames
               if (cmd.item) queryParams.item = cmd.item
               if (cmd.itemBias != null) queryParams.itemBias = cmd.itemBias
               if (cmd.user) queryParams.user = cmd.user
               if (cmd.userBias!=null) queryParams.userBias = cmd.userBias
               if (cmd.num) queryParams.num = cmd.num

               JsonObject jsonObject = engine.sendQuery(queryParams)
               if (jsonObject)
               {
                  result.message = jsonObject.toString()
                  result.contentType = "application/json"
               }
            } else
            {
               result.status = HttpStatus.INTERNAL_SERVER_ERROR
               result.message = "App access key lookup failed for app name ${cmd.appName}."

               return result
            }
         } else
         {
            result.status = HttpStatus.BAD_REQUEST
            result.message = "PredictiveIO App Instance not found for appName ${cmd.appName}"
         }
      } else
      {
         def messages = []
         String message = "Invalid parameters"
         //cmd.allErrors.each{messages << "${it}"}
         //message = messages.join("\n").toString()
         result = [status : HttpStatus.BAD_REQUEST,
                   message: message.toString()]
      }

      result
   }

   def indexData(PredioIndexDataCommand cmd) {
      HashMap result = [status     : HttpStatus.OK,
                        message    : "Success",
                        contentType: "text/plain"]

      if(!OmarPredioUtils.predioConfig.enabled)
      {
         result.status  = HttpStatus.METHOD_NOT_ALLOWED
         result.message = "PredictionIO is currently disabled"

         return result
      }
      String dateRanges = cmd.dateRanges
      String locationFields = cmd.locationFields
      String categoryFields = cmd.categoryFields
      String dateField      = cmd.dateField
      String expirePeriod   = cmd.expirePeriod
      def config = OmarPredioUtils.predioConfig

      //default if not present
      if(!cmd.wfsUrl){
         def wfsParams = config.index.wfs.params.inject([]){resultList,k,v-> resultList<<"${k}=${v}"}.join("&")
         cmd.wfsUrl = "${config.index.wfs.baseUrl}?${wfsParams}"
      }
      if(!cmd.locationFields)
      {
         cmd.locationFields = config.index.fields.locations.join(",")
      }
      if(!cmd.categoryFields)
      {
         cmd.categoryFields = config.index.fields.categories.join(",")
      }
      if(!cmd.dateField)
      {
         cmd.dateField = config.index.dateField
      }
      if(!cmd.idField)
      {
         cmd.idField = config.index.idField
      }

      try{
         def indexJobRecord = new PredioIndexJob(cmd.properties)
         indexJobRecord.save(flush:true)
      }
      catch(e)
      {
         result = [status : HttpStatus.BAD_REQUEST,
                   message: message.toString()]
      }
//        def timezone = TimeZone.getTimeZone("UTC")
//        if(cmd.dateRanges)
//        {
//            def dateRanges = DateUtil.parseOgcTimeIntervalsAsDateTime(cmd.dateRanges)//, DateTimeZone.UTC
//            println dateRanges
//
//            def expirePeriod = DateUtil.parsePeriod(cmd.expirePeriod)
//            println expirePeriod
//        }

      result

   }

}