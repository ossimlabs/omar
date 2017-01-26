package oldmar.app

class WfsProxyController
{
  def wfsProxyService

  def index()
  {
    render wfsProxyService.handleRequest( params )
  }
}
