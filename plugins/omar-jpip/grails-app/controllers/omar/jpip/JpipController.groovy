package omar.jpip

import omar.core.BindUtil

class JpipController {

    def jpipService

    def index() {
        render ""

    }

    def stream()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new ConvertCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( ConvertCommand, requestParams )
        bindData( cmd, requestParams )

        def jsonResult = jpipService.stream( cmd )

        if ( jsonResult == null )
        {
            response.sendError(404)
        }
        else
        {
            render( jsonResult )
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
