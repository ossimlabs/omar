package omar.ingest.metrics

import grails.converters.JSON
import omar.core.BindUtil

class IngestMetricsController {
    def ingestMetricsService

    def index() {
        render ""
    }

    def startIngest(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result = ingestMetricsService.startIngest(cmd)
        String resultString =  (result as JSON).toString()
        response.status      = result.statusCode
        response.contentLength = resultString.length()

        render contentType: "application/json", text: result as JSON
    }

    def endIngest(){

        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result = ingestMetricsService.endIngest(cmd)
        String resultString =  (result as JSON).toString()
        response.status      = result.statusCode
        response.contentLength = resultString.length()


        render contentType: "application/json", text: resultString
    }
    def startCopy(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.startCopy(cmd)
        String resultString =  (result as JSON).toString()
        response.status      = result.statusCode
        response.contentLength = resultString.length()


        render contentType: "application/json", text: resultString
    }
    def endCopy(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.endCopy(cmd)
        String resultString =  (result as JSON).toString()
        response.status      = result.statusCode
        response.contentLength = resultString.length()


        render contentType: "application/json", text: resultString
    }
    def startStaging(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.startStaging(cmd)
        String resultString =  (result as JSON).toString()
        response.status     = result.statusCode
        response.contentLength     = resultString.length()


        render contentType: "application/json", text: resultString
    }
    def endStaging(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.endStaging(cmd)
        String resultString =  (result as JSON).toString()
        response.status     = result.statusCode
        response.contentLength     = resultString.length()


        render contentType: "application/json", text: resultString
    }
    def update(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IngestCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IngestCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.update(cmd)
        String resultString =  (result as JSON).toString()
        response.status     = result.statusCode
        response.contentLength     = resultString.length()


        render contentType: "application/json", text: resultString
    }

    def delete()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new DeleteCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( DeleteCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.delete(cmd)
        String resultString =  (result as JSON).toString()
        response.status     = result.statusCode
        response.contentLength     = resultString.length()

        render contentType: "application/json", text: resultString
    }
    def list()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        ListCommand cmd = new ListCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( ListCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.list(cmd)
        String resultString    =  (result as JSON).toString()
        response.status        = result.statusCode
        response.contentLength = resultString.length()

        render contentType: "application/json", text: resultString
    }
    def summary()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        SummaryCommand cmd = new SummaryCommand()
        if(jsonData) requestParams << jsonData

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( SummaryCommand, requestParams )
        bindData( cmd, requestParams )

        HashMap result  = ingestMetricsService.summary(cmd)
        String resultString =  (result as JSON).toString()
        response.status     = result.statusCode
        response.contentLength     = resultString.length()


        render contentType: "application/json", text: resultString
    }
}
