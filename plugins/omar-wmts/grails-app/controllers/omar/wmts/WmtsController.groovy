package omar.wmts

//import grails.plugin.springsecurity.annotation.Secured
import omar.core.BindUtil

class WmtsController {

    def webMapTileService
//    @Secured( ['IS_AUTHENTICATED_ANONYMOUSLY'] )
    def index() {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
        def cmd = new WmtsCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( WmtsCommand, requestParams )
        bindData( cmd, requestParams )
        try
        {
            if ( cmd.validate() )
            {

                switch ( cmd.request?.toUpperCase() )
                {
                    case 'GETCAPABILITIES':
                        forward action: 'getCapabilities'
                        break
                    case 'GETTILE':
                        forward action: 'getTile'
                        break
                    default:
                        throw new Exception( "Operation ${cmd.request} is not supported" )
                }
            }
            else
            {
                throw new Exception( cmd.errors.allErrors.collect { messageSource.getMessage( it, null ) }.join( '\n' ) )
            }
        }
        catch ( e )
        {
            log.error e.message.toString()
            println e.message
            render e.toString()
          //  render contentType: 'application/xml', text: exceptionService.createMessage( e.message )
        }

    }
//    @Secured( ['IS_AUTHENTICATED_ANONYMOUSLY'] )
    def getCapabilities()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
        def cmd = new GetCapabilitiesCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( GetCapabilitiesCommand, requestParams )
        bindData( cmd, requestParams )
        try
        {
            def results = webMapTileService.getCapabilities( getBaseUrl(), cmd )

            render contentType: results.contentType, text: results.buffer.toString()
        }
        catch ( e )
        {
           // println "***************************************"
           // e.printStackTrace()
            render e.toString()
            //println "*"*40
            //e.printStackTrace()
            //println "*"*40
           // render( contentType: 'application/xml', text: exceptionService.createMessage( e.message ) )
        }
    }

//    @Secured( ['IS_AUTHENTICATED_ANONYMOUSLY'] )
    def getTile()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
        def cmd = new GetTileCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( GetTileCommand, requestParams )
        bindData( cmd, requestParams )
        try
        {
            def result = webMapTileService.getTile( cmd )

            if(result.contentType) response.contentType = result.contentType
            if(result.data?.length) response.contentLength = result.data.length
            response.status = result.status
            response.outputStream.write(result.data)
        }
        catch ( e )
        {
            println e.message
//            e.printStackTrace()
            render e.toString()
        }
    }
}
