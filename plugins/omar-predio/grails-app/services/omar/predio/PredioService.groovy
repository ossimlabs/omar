package omar.predio
import io.prediction.Event;
import io.prediction.EventClient
import io.prediction.EngineClient
import grails.transaction.Transactional
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
      if(cmd.categories)
      {
         properties.categories = []
         cmd.categories?.split(",")?.each{String category->
            properties.categories << category.trim()
         }
      }
      if(cmd.locations)
      {
         properties.locations = []
         cmd.locations?.split(",")?.each{String location->
            properties.locations << location.trim()
         }
      }
      if(cmd.expireDate) properties.expireDate = cmd.expireDate
      if(cmd.eventTime) universalCmd.eventTime = cmd.eventTime
      universalCmd.properties = properties?:null


      println universalCmd

      sendUniversalEvent(universalCmd)
   }

   def getUserRecommendations(UserRecommendationCommand cmd)
   {
      // setup universal command and call the service
      UniversalQueryCommand universalCmd = new UniversalQueryCommand()
      universalCmd.user    = cmd.user
      universalCmd.appName = cmd.appName
      universalCmd.num     = cmd.num

      def locations = []
      def categories = []

      def fields = []

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
         if(cmd.categoryBiasBias!=null)
         {
            mapping.bias = cmd.categoryBiasBias
         }
         fields << mapping
      }

      if(fields) universalCmd.fields = fields


      getRecommendations(universalCmd)
   }
   def getRecommendations(UniversalQueryCommand cmd)
   {
      HashMap result = [status     : HttpStatus.OK,
                        message    : [],
                        contentType: "text/plain"]

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
               if (cmd.eventNames) queryParams.eventNames = cmd.eventNames
               if (cmd.item) queryParams.item = cmd.item
               if (cmd.user) queryParams.user = cmd.user
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
}