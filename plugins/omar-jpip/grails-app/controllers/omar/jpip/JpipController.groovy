package omar.jpip

import grails.converters.JSON
import omar.core.BindUtil

class JpipController {

    def jpipService

    def index() {
        render ""

    }

    def stream()
    {
	try
        {
            def jsonData = request.JSON ? request.JSON as HashMap : null
            def requestParams = params - params.subMap(['controller', 'action'])
            def cmd = new ConvertCommand()

            // get map from JSON and merge into parameters
            if (jsonData) requestParams << jsonData
            BindUtil.fixParamNames(ConvertCommand, requestParams)
            bindData(cmd, requestParams)

            HashMap result = jpipService.stream(cmd)
            if (result == null) {
                response.sendError(404)
            } else {
                render contentType: "application/json", text: (result as JSON).toString()
            }
        }
        catch ( e )
        {
            // log.error e.message.toString()
            response.status = 400
            // println e.message
            render e.toString()
            //  render contentType: 'application/xml', text: exceptionService.createMessage( e.message )
        }
    }

   /*
    def convert() {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new ConvertCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( ConvertCommand, requestParams )
        bindData( cmd, requestParams )

        jpipService.convert(cmd)

        render "Hello"
    }
    */
}
