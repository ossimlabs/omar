package omar.app

class OmarController {
	
    def index() 
    {
    	URL url = new URL (request.requestURL.toString())

    	def urlString = "${url.protocol}://${url.host}${url.port?':'+url.port:''}"
    	println url.host

		def clientConfig = new ConfigObject()
		clientConfig.params = grailsApplication.config.omar.app

		clientConfig.serverURL = grailsApplication.config.grails.serverURL
		
		if (!clientConfig.serverURL) {

			clientConfig.serverURL = "${url.protocol}://${url.host}${url.port?':'+url.port:''}"

		}
		
		// Params to pass to client
		def clientParams = grailsApplication.config.omar.app
		println grailsApplication.config.grails.serverURL

		//println "clientParams: ${ clientParams }"

		[
				// clientConfig: [
				// 		clientParams: clientParams
				// ]
				// clientConfig: [
				// 	clientConfig: clientConfig
				// ]
				clientConfig: clientConfig
		]

	}
}
