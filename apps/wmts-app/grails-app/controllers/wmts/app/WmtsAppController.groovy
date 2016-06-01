package wmts.app
import omar.openlayers.OmarOpenlayersUtils
import omar.wmts.OmarWmtsUtils
import java.net.URL
class WmtsAppController {

    def index() {

    def clientConfig = new ConfigObject()
		clientConfig.params = grailsApplication.config.omar.app

		clientConfig.openlayers = OmarOpenlayersUtils.openlayersConfig
		 // Use Enhancer traits from omar-core getBaseUrl()
    clientConfig.wmts = OmarWmtsUtils.wmtsConfig
		clientConfig.serverURL = getBaseUrl()
    clientConfig.wmtsURL = "${clientConfig.serverURL}/wmts"
		// Params to pass to client
		def clientParams = grailsApplication.config.omar.app

		[
				clientConfig: clientConfig
		]

    }
}
