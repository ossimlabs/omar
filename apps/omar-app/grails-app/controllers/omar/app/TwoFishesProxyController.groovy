package omar.app

class TwoFishesProxyController
{
  def index()
  {
    def twoFishesServer = grailsApplication.config.twoFishesServer
    def twoFishesURL = grailsLinkGenerator.link( base: twoFishesServer, params: params ).toURL()

    byte[] data = twoFishesURL.bytes

    println new String( data )

    render contentType: 'application/json', file: data
  }
}
