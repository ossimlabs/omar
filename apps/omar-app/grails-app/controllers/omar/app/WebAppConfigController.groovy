package omar.app

import grails.converters.JSON

class WebAppConfigController {

    def index() {

        def webconfig = grailsApplication.config.webconfig
        webconfig.misc.icons.greenMarker = asset.assetPath(src:webconfig.misc.icons.'green-marker' as String)

        render contentType: "application/json", text: webconfig as JSON
    }
}
