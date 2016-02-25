package omar.predio

import omar.core.BindUtil
import com.github.rahulsom.swaggydoc.*
import com.wordnik.swagger.annotations.*

@Api(value = "predio",
        description = "API operations for Prediction IO",
        produces = 'application/json',
        consumes = 'application/json'
)
class PredioController
{
   static allowedMethods = [index:["GET"],
                            viewItem:["POST", "GET"],
                            setItem:["POST"],
                            getPopularItems:["GET"],
                            getRecommendations:["POST","GET"],
                            getUserRecommendations:["GET"],
                            getItemRecommendations:["GET"]
   ]
   def predioService

   def index()
   {
      render ""
   }

   @ApiOperation(value = "View an item", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'appName', value = 'Application name', allowableValues="[omar_universal]", defaultValue = 'omar_universal', required=true, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'entityType', value = 'Entity type', allowableValues="[user]",  defaultValue = 'user', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'entityId', value = 'User id', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'targetEntityType', value = 'item', allowableValues="[item]", defaultValue = 'item', paramType = 'query', dataType = 'int'),
           @ApiImplicitParam(name = 'targetEntityId', value = 'Id of the item being viewed', paramType = 'query', dataType = 'int'),
   ])
   def viewItem()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new ViewCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( ViewCommand, requestParams )
      bindData( cmd, requestParams )
      cmd.entityId = cmd.entityId?:"anonymous"
      cmd.targetEntityType = cmd.targetEntityType?:"item"

      UniversalEventCommand universalCommand = new UniversalEventCommand()
      universalCommand.entityId = cmd.entityId
      universalCommand.targetEntityId = cmd.targetEntityId
      universalCommand.targetEntityType = cmd.targetEntityType
      universalCommand.event = "view"
      universalCommand.entityType = "user"


      HashMap result = predioService.sendUniversalEvent(universalCommand)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }

   @ApiOperation(value = "Set an item",
                 consumes= 'application/json',
                 produces='application/json',
                 httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'item', value = 'Id of the item to modify or set', paramType = 'query', dataType = 'int'),
           @ApiImplicitParam(name = 'categories', value = 'Comma seperated list of categorie', paramType = 'query', dataType = 'int'),
           @ApiImplicitParam(name = 'locations', value = 'Comma seperated list of locations', paramType = 'query', dataType = 'int'),
           @ApiImplicitParam(name = 'eventTime', value = 'Time of the event (ISO date)', paramType = 'query', dataType = 'int'),
           @ApiImplicitParam(name = 'expireDate', value = 'Time of Expiration', paramType = 'query', dataType = 'int'),
   ])
   def setItem()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new SetItemCommand()
      if(jsonData) requestParams << jsonData

      BindUtil.fixParamNames( SetItemCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = predioService.setItem(cmd)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }
   @ApiOperation(value = "Get popular items", produces='application/json', httpMethod = "GET")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'appName', value = 'Application name', allowableValues="[omar_universal]", defaultValue = 'omar_universal', required=true, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'num', value = 'Maximum results', defaultValue = '10', paramType = 'query', dataType = 'int'),
   ])
   def getPopularItems()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new UniversalQueryCommand()
      if(jsonData) requestParams << jsonData

      BindUtil.fixParamNames( UniversalQueryCommand, requestParams )
      bindData( cmd, requestParams )
      cmd.item = null
      cmd.user = null
      cmd.dateRange = null
      cmd.eventNames = null
      cmd.fields = null
      HashMap result = predioService.getRecommendations(cmd)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }

   @ApiOperation(value = "Get recommendations", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'body',
                   value = "General Message for querying recommendations",
                   defaultValue = """{
   "user": "<Replace with id of the user>",
   "item": "<Id of the item we are viewing>",
   "num": 10,
    "eventNames": ["<comma separate list of event, example 'view'>"],
    "fields": [{
        "name": "categories",
        "values": ["<comma separated list of categories>"],
        "bias": <optional bias>
    }],
   "dateRange": {
            "name": "date",
            "after": "<iso date string for after this date>"
            "before": "<iso date string for before this date>"
   }
    }                 """,
                   paramType = 'body',
                   dataType = 'string')
   ])
   def getRecommendations()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new UniversalQueryCommand()
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( UniversalQueryCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = predioService.getRecommendations(cmd)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }

   @ApiOperation(value = "Get item recommendations", produces='application/json', httpMethod = "GET")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'appName', value = 'Application name', allowableValues="[omar_universal]", defaultValue = 'omar_universal', required=true, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'item', value = 'Item id', defaultValue = '', paramType = 'query', required=false, dataType = 'string'),
           @ApiImplicitParam(name = 'itemBias', value = 'Item bias', defaultValue = '', paramType = 'query', dataType = 'float'),
           @ApiImplicitParam(name = 'locations', value = 'Locations', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'locationBias', value = 'Location bias', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'categories', value = 'Categories', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'categoryBias', value = 'Category bias', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'beforeAvailableDate', value = 'Before Available Date', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'afterAvailableDate', value = 'After Available Date', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'useCurrentDateFilter', value = 'Use Current Date Filter', allowableValues="[true,false]", defaultValue = 'true', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'num', value = 'Maximum results', defaultValue = '10', paramType = 'query', dataType = 'int'),
   ])
   def getItemRecommendations()
   {
      // bind with JSON or URL params
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new ItemRecommendationCommand()
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( ItemRecommendationCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = predioService.getItemRecommendations(cmd)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }

   @ApiOperation(value = "Get user recommendations", produces='application/json', httpMethod = "GET")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'appName', value = 'Application name', allowableValues="[omar_universal]", defaultValue = 'omar_universal', required=true, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'user', value = 'User id', defaultValue = '', paramType = 'query', required=true, dataType = 'string'),
           @ApiImplicitParam(name = 'userBias', value = 'User bias', defaultValue = '', paramType = 'query', dataType = 'float'),
           @ApiImplicitParam(name = 'locations', value = 'Locations', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'locationBias', value = 'Location bias', defaultValue = '', paramType = 'query', dataType = 'float'),
           @ApiImplicitParam(name = 'categories', value = 'Categories', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'categoryBias', value = 'Category bias', defaultValue = '', paramType = 'query', dataType = 'float'),
           @ApiImplicitParam(name = 'beforeAvailableDate', value = 'Before Available Date', defaultValue = '', paramType = 'query', dataType = 'date'),
           @ApiImplicitParam(name = 'afterAvailableDate', value = 'After Available Date', defaultValue = '', paramType = 'query', dataType = 'date'),
           @ApiImplicitParam(name = 'useCurrentDateFilter', value = 'Use Current Date Filter', allowableValues="[true,false]", defaultValue = 'true', paramType = 'query', dataType = 'boolean'),
           @ApiImplicitParam(name = 'num', value = 'Maximum results', defaultValue = '10', paramType = 'query', dataType = 'int'),
   ])
   def getUserRecommendations()
   {
      // bind with JSON or URL params
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new UserRecommendationCommand()
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( UserRecommendationCommand, requestParams )
      bindData( cmd, requestParams )

      HashMap result = predioService.getUserRecommendations(cmd)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }
}
