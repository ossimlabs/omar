package geoscript.app

class EvwhsController
{
    def evwhsService

    def index()
    {

    }

    def blah()
    {
      render evwhsService.blah(params)      
    }

    def wms()
    {
      render evwhsService.wms(params)
    }
}
