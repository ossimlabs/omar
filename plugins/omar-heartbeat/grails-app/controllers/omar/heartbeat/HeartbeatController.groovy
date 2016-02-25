package omar.heartbeat
import grails.converters.JSON

class HeartbeatController {
  // def grailsApplication

    def index() {
        def result = [
                status: "alive",
                name: grailsApplication.config.info.app.name,
                version: grailsApplication.config.info.app.version
        ]
        render result as JSON
    }
}
