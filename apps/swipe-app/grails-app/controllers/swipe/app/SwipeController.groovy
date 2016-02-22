package swipe.app


class SwipeController
{
	def index()
	{
		//def banner = grailsApplication.config.classificationBanner
		def clientParams = grailsApplication.config.swipe.app

		println "clientParams: ${ clientParams }"

		[
				clientConfig: [
						clientParams: clientParams
				]
		]

	}

}
