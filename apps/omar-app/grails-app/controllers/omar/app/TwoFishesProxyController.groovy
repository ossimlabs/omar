package omar.app

class TwoFishesProxyController
{
  def index()
  {
    def twoFishesServer = grailsApplication.config.twoFishesServer
    def twoFishesURL = grailsLinkGenerator.link( base: twoFishesServer, params: params ).toURL()

    render contentType: 'application/json', file: twoFishesURL.bytes
  }
}
