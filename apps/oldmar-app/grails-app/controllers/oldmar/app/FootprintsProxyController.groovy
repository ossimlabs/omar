package oldmar.app

class FootprintsProxyController
{
  def footprintsProxyService

  def index()
  {
    render footprintsProxyService.handleRequest( params )
  }
}
