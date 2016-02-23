package swipe.app


class SwipeController
{
	def index()
	{

		def clientParams = grailsApplication.config.swipe.app

		println "clientParams: ${ clientParams }"

		[
				clientConfig: [
						clientParams: clientParams
				]
		]

	}

}
