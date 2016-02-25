package omar.heartbeat
import grails.converters.JSON

class HeartbeatController {
  // def grailsApplication

    def index() {
        println grailsApplication.config.info
        def result = grailsApplication.config.info
//        def result = [
//                status: "alive",
//                name: grailsApplication.config.info.app.name,
//                version: grailsApplication.config.info.app.version
//        ]
        render result as JSON
    }
}
