package omar.app

class OmarController {
	
    def index() 
    {
		def clientConfig = new ConfigObject()
		clientConfig.params = grailsApplication.config.omar.app

		 // Use Enhancer traits from omar-core getBaseUrl()
		clientConfig.serverURL = getBaseUrl()

		// Params to pass to client
		def clientParams = grailsApplication.config.omar.app

		[
				clientConfig: clientConfig
		]
	}
}
