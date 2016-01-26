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
   static allowedMethods = [index:'GET',
                            rate:['POST', 'GET'],
                            showTrending:["GET"]
   ]

   def predioService

   def index()
   {
      render ""
   }
   @ApiOperation(value = "Rate an item", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'appName', value = 'Event App ID for Prediciton IO', allowableValues="[omar_recommendation,omar_trending]", defaultValue = 'omar_recommendation', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'event', value = 'Type of event', allowableValues="[rate]", defaultValue = 'rate', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'entityType', value = 'Entity type', allowableValues="[user]", defaultValue = 'user', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'entityId', value = 'Id of the Entity', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'targetEntityType', value = 'Target entity type', allowableValues="[item]", defaultValue="item", paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'targetEntityId', value = 'Id of the target we are rating', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'rating', allowableValues="[1,2,3,4,5]", value = 'Rating value from 1 to 5', paramType = 'query', dataType = 'float'),
   ])
   def rate()
   {
      def rateParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new RateCommand()
      BindUtil.fixParamNames( RateCommand, rateParams )
      bindData( cmd, rateParams )

      HashMap result = predioService.rate(cmd)

      response.status      = result.status.value()
      response.contentType = result.contentType
      render result.message
   }

   @ApiOperation(value = "Show trending items", produces='plain/text', httpMethod = "GET")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'appName', allowableValues="[omar_trending]", value = 'Event App ID for Prediciton IO for trending items', defaultValue = 'omar_trending', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'entityId', value = 'Entity/user', allowableValues="[all]", defaultValue = 'all', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'maxCount', value = 'Maximum results', defaultValue = '10', paramType = 'query', dataType = 'int'),
   ])
   def showTrending()
   {
      def showTrendingParams = params - params.subMap( ['controller', 'format', 'action'] )
      def cmd = new ShowTrendingCommand()
      BindUtil.fixParamNames( ShowTrendingCommand, showTrendingParams )
      bindData( cmd, showTrendingParams )
      HashMap result = predioService.showTrending(cmd)


      response.status      = result.status.value()
      response.contentType = response.contentType

      render result.message
   }
}
