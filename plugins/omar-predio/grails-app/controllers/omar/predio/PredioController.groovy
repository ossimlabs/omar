package omar.predio

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

//      println "*"*40
//      println cmd
      HashMap result = predioService.rate(cmd)

      response.status = result.status.value()
      response.contentType = response.contentType
      render result.message
   }

}
