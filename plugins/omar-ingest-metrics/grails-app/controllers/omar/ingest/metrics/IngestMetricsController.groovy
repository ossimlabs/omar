package omar.ingest.metrics

import grails.converters.JSON
import omar.core.BindUtil

class IngestMetricsController {

    def index() { }
    def startIngest(){
        println "PARAMS ========== ${params} "
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

       // println cmd

        render ""
    }
    def endIngest(){

        render ""
    }

}
