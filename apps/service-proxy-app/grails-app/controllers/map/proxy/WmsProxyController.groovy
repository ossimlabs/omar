package map.proxy

class WmsProxyController
{
  def wmsProxyService

  def index()
  {
    response.setDateHeader('Expires', System.currentTimeMillis() + 60*60*1000)
    render wmsProxyService.handleRequest( params )
  }
}
