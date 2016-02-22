package swipe.app


class SwipeController
{
	def index()
	{
		def banner = grailsApplication.config.classificationBanner

		println "banner: ${ banner }"

		[
				initParams: [
						banner: banner
				]
		]

	}

//    def swipe()
//    {
//		[
//        	initParams: [
//            	banner: grailsApplication.config.classificationBanner
//        	] as JSON
//    	]
//
//    }
}
