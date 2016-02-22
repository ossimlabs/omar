package swipe.app

class SwipeController {

    def index() 
    { 
    	[
        	initParams: [
            	banner: grailsApplication.config.swipe.app.classificationBanner
        	] as JSON
    	]

    }

    def swipe() 
    {
		[
        	initParams: [
            	banner: grailsApplication.config.classificationBanner
        	] as JSON
    	]

    }
}
