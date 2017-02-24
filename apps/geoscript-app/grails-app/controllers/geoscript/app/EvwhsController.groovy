package geoscript.app

import omar.openlayers.OpenLayersConfig

class EvwhsController
{
    def evwhsService

    OpenLayersConfig openLayersConfig

    def index()
    {
      [mapViewParams: [
        contextPath: grailsApplication.config.server.contextPath,
        openlayers: openLayersConfig
        ]
      ]
    }

    def wms()
    {
      render evwhsService.wms(params)
    }
}
