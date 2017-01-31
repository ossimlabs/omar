package oldmar.app

class WfsProxyController
{
  def wfsProxyService

  def index()
  {
    switch ( request.method.toUpperCase()  )
    {
    case 'GET':
      render wfsProxyService.handleRequestGET( params )
      break
    case 'POST':
      render wfsProxyService.handleRequestPOST( request.XML )
      break
    }
  }
}
