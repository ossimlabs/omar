package omar.app

class TwoFishesProxyController
{
  def index()
  {
    //def twoFishesServer = grailsApplication.config.webconfig.services.twofishes.url
    def twoFishesServer = grailsApplication.config.omar.app.twofishes.baseUrl
    def twoFishesURL = grailsLinkGenerator.link( base: twoFishesServer, params: params ).toURL()

    byte[] data = twoFishesURL.bytes

    //println new String( data )

    render contentType: 'application/json', file: data
  }
}
