package omar.app

import grails.converters.JSON

class WebAppConfigController {

    def index() {
        render contentType: "application/json", text: grailsApplication.config.webconfig as JSON
    }
}
