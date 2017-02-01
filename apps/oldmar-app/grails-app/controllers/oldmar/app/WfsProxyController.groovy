package oldmar.app

class WfsProxyController
{
  static allowedMethods = [index: ['GET', 'POST', 'OPTIONS']]

  def wfsProxyService

  def index()
  {
    switch ( request.method.toUpperCase() )
    {
    case 'GET':
      render wfsProxyService.handleRequestGET( params )
      break
    case 'POST':
      render wfsProxyService.handleRequestPOST( request.XML )
      break
    case 'OPTIONS':
      render contextPath: 'text/plain', text: "${new Date().format( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" )}"
      break
    }
  }
}
