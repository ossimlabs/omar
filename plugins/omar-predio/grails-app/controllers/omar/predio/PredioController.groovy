package omar.predio

class PredioController
{
   def predioService

   def index()
   {
      render ""
   }
   def rate(RateCommand cmd)
   {
      HashMap result = predioService.rate(cmd)
      response.status = result.status.value()
      response.contentType = response.contentType

      render result.message
   }

}
