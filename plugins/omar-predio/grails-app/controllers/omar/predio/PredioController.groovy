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
   def predioService

   def index()
   {
      render ""
   }
   @ApiOperation(value = "Rate an item", produces='application/json')
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'appName', value = 'Event App ID for Prediciton IO', defaultValue = 'omar_recommendation', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'event', value = 'Type of event', defaultValue = 'rate', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'entityType', value = 'Entity type', defaultValue = 'user', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'entityId', value = 'Id of the Entity', defaultValue = '', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'targetEntityType', value = 'Target entity type', defaultValue="item", paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'targetEntityId', value = 'Id of the target we are rating', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'rating', value = 'Rating value from 1 to 5', paramType = 'query', dataType = 'float'),
   ])
   @ApiResponses([
           @ApiResponse(code = 400, message = 'Bad Request'),
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

   @ApiOperation(value = "Show trending items")
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
