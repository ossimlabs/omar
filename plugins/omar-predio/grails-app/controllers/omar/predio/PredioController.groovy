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


   @ApiOperation(value = "Index data via WFS into PredictionIO database",
           produces='application/json',
           httpMethod = "POST",
           notes = """
    <ul>
        <li>
            <b>wfsUrl</b><p/>
            Is an optional parameter and serves as an override.  If not specified then the default
            URL setup by the configuration of this service will be used.  If it's specified then
            the full parameters must be given for the <b>wfsUrl</b>. Example:<p>
            http://foo.example.com/ogc/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&typeName=omar:raster_entry, ....etc.
        </li>
        <br>
        <li>
            <b>dateRanges</b><p/>Is based on an ISO8601 time interval which is of the form:
<pre>
Start and end, such as:      2007-03-01T13:00:00Z/2008-05-11T15:30:00Z
Start and duration, such as: 2007-03-01T13:00:00Z/P1Y2M10DT2H30M
Duration and end, such as:   P1Y2M10DT2H30M/2008-05-11T15:30:00Z
</pre>
            </br>
            For full description of interval period format please see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
        </li>
        <br>
        <li>
        <b>locationFields</b><p/>
        Is a comma separated list of WFS feature fields that will be used for the location strings.
        </li>
        <br>
        <li>
        <b>categoryFields</b><p/>
        Is a comma separated list of WFS feature fields that will be used for the category strings.
        </li>
        <br>
        <li>
        <b>dateField</b><p/>
        Defines which feature field is used as the event date for PredictionIO.   By default the
        acquisition_date is used. If that is not found then the date will default to current date.
        </li>
        <br>
        <li>
        <b>expirePeriod</b><p/>
        Is an ISO8601 Period format that describes how long until the image expires.  For example,
        if we specify P1Y then whatever the <b>dateField</b> value is it will mark the event to expire
        after 1 year from that date.
        </li>
    </ul>
    """)
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'wfsUrl', value = "Base WFS url override", defaultValue = '', required=false, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'dateRanges', value = 'Date ranges to add', defaultValue = '', paramType = 'query', required=false, dataType = 'string'),
           @ApiImplicitParam(name = 'locationFields', value = 'WFS Location fields', defaultValue = 'country_code,be_number', allowMultiple= true, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'categoryFields', value = 'WFS Category fields', defaultValue = 'mission_id,image_category,sensor_id', allowMultiple=true, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'dateField', value = 'WFS field for date', defaultValue = 'acquisition_date', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'expirePeriod', value = 'ISO8601 Period', defaultValue = 'P3D', paramType = 'query', dataType = 'string'),
   ])
   def indexData()
   {
      HashMap result
      // bind with JSON or URL params
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new PredioIndexDataCommand()
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( PredioIndexDataCommand, requestParams )
      bindData( cmd, requestParams )

      result = predioService.indexData(cmd)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }

}
