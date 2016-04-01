package omar.jpip

import omar.core.BindUtil

class JpipController {

    def jpipService

    def index() {
        render ""
    }

    def convert(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new ConvertCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( ConvertCommand, requestParams )
        bindData( cmd, requestParams )


        jpipService.convert(cmd)

//        println cmd

        render "Hello"

    }
}
