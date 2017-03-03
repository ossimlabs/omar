package wmts.app
// import omar.openlayers.OmarOpenlayersUtils
// import omar.wmts.OmarWmtsUtils

import omar.openlayers.OpenLayersConfig

class WmtsAppController {

  OpenLayersConfig openLayersConfig

    def index() {

    // def clientConfig = new ConfigObject()
		// clientConfig.params = grailsApplication.config.omar.app
    //
		// clientConfig.openlayers = OmarOpenlayersUtils.openlayersConfig
		//  // Use Enhancer traits from omar-core getBaseUrl()
    // clientConfig.wmts = OmarWmtsUtils.wmtsConfig
		// clientConfig.serverURL = getBaseUrl()
    // clientConfig.wmtsURL = "${clientConfig.serverURL}/wmts"
		// // Params to pass to client
		// def clientParams = grailsApplication.config.omar.app
    //

      def clientConfig = [
        wmts: grailsApplication.config.omar.wmts,
        serverURL: getBaseUrl(),
        openlayers: openLayersConfig,
        params: grailsApplication.config.omar.app,
        wmtsURL: "${getBaseUrl()}/wmts"
      ]

		  [clientConfig: clientConfig ]
    }
}
