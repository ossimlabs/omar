package map.proxy

class WmsProxyController
{
  def wmsProxyService

  def index()
  {
    //response.setDateHeader('Expires', System.currentTimeMillis() + 60*60*1000 )
    response.setDateHeader('Expires', System.currentTimeMillis() + 2419200000 ) // One month
    render wmsProxyService.handleRequest( params )
  }
}
