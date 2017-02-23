package geoscript.app

import org.springframework.beans.factory.annotation.Value

class EvwhsController
{
    def evwhsService

    @Value('${geoscript.app.refMap.url}')
    def refMapURL

    @Value('${geoscript.app.refMap.layer}')
    def refMapLayer

    def index()
    {
      [mapViewParams: [
        contextPath: grailsApplication.config.server.contextPath,
        refMap: [
          url: refMapURL,
          layer: refMapLayer
          ]
        ]
      ]
    }

    def wms()
    {
      render evwhsService.wms(params)
    }
}
