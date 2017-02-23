package geoscript.app

class EvwhsController
{
    def evwhsService

    def index()
    {
      [mapViewParams: [contextPath: grailsApplication.config.server.contextPath]]
    }

    def wms()
    {
      render evwhsService.wms(params)
    }
}
