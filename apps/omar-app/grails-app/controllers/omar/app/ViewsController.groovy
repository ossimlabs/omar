package omar.app

class ViewsController {
    def renderView() { 
	def tmpl = request.forwardURI
        def start = ( grailsApplication.config.server.contextPath ) ? 3 : 2

        tmpl = tmpl.split('/')[start..-1].join('/')
	render(template: tmpl)
    }
}
