package omar.predio

import grails.converters.JSON
import omar.core.BindUtil

class PredioController
{
   def predioService

   def index()
   {
      render ""
   }
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
