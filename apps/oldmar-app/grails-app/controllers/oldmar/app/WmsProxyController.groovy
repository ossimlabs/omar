package oldmar.app

class WmsProxyController
{
  def wmsProxyService

  def index()
  {
    render wmsProxyService.handleRequest( params )
  }
}
