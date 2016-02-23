package swipe.app


class SwipeController
{
	def index()
	{
		// Params to pass to client
		def clientParams = grailsApplication.config.swipe.app

		println "clientParams: ${ clientParams }"

		[
				clientConfig: [
						clientParams: clientParams
				]
		]

	}

}
