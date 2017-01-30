package map.proxy

class WmsProxyController
{
  def wmsProxyService

  def index()
  {
    render wmsProxyService.handleRequest( params )
  }
}
