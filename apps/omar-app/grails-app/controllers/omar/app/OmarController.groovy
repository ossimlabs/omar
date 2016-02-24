package omar.app

class OmarController {

    def index() 
    {
		// Params to pass to client
		def clientParams = grailsApplication.config.omar.app

		println "clientParams: ${ clientParams }"

		[
				clientConfig: [
						clientParams: clientParams
				]
		]

	}
}
