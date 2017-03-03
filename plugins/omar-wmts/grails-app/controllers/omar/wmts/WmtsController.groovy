package omar.wmts

import grails.converters.JSON

//import grails.plugin.springsecurity.annotation.Secured
import omar.core.BindUtil
import com.github.rahulsom.swaggydoc.*
import com.wordnik.swagger.annotations.*

@Api(value = "wmts",
        description = "WMTS Support"
)
class WmtsController {

    def webMapTileService
//    @Secured( ['IS_AUTHENTICATED_ANONYMOUSLY'] )
    @ApiOperation( value = "OGC WMTS service", produces = 'application/vnd.ogc.wms_xml',
            notes=""" This is the main entry point for OGC WMTS services.
<br>
<br>
    <ul>
        <li>
            <b>service</b><p/>
            Is a required parameter that has a fixed value of WMTS
        </li>
        <br><br>
        <li>
            <b>version</b><p/>
            Version 1.0.0 is supported
        </li>
        <br><br>
        <li>
            <b>request</b><p/>
            The only supported requests are GetCapabilities or GetTile.  If GetCapabilities is selected then
            the only required parameters that must be filled are service, version, and request.  If GetTile is selected then you must
            also enter values for fields format, tileRow, tileCol, tileMatrix, and tileMatrixSet
        </li>
        <br><br>
        <li>
            <b>layer</b><p/>
            This is the layer name for the GetTile.
        </li>
        <br><br>
        <li>
            <b>format</b><p/>
            Defines the output format of the tile if the request is GetTile.  The currently supported formats
            can be image/jpeg or image/png
        </li>
        <br><br>
        <li>
            <b>tileRow</b><p/>
            Tile numbering starts in the upper left corner of a pyramid.  So tileRow = 0 refers to the top row of
            tiles.  This is the tileRow index for the tileMatrix.
        </li>
        <br><br>
        <li>
            <b>tileCol</b><p/>
            Tile numbering starts in the upper left corner of a pyramid.  So tileCol = 0 refers to the first column of
            tiles.  This is the tileCol index for the tileMatrix.
        </li>
        <br><br>
        <li>
            <b>tileMatrix</b><p/>
            Character String type, not empty.  This is string identifier.  On our server the capabilities will list this as
            identifiers from 0 .. maxLeve.  This will have a more 1 to 1 mapping to the level/row/col format where the tileMatrix id refers
            to the level and level 0 is the coarsest resolution down to the highest resolution of maxLevel.
        </li>
        <br><br>
        <li>
            <b>tileMatrixSet</b><p/>
            Character String type, not empty. This is the Set idientifier.  This can be read from the capabilites.  By default we
            have the WorldGeographic is a default MatrixSet identifier
        </li>
        <br>
    </ul>
"""
    )
    @ApiImplicitParams( [
            @ApiImplicitParam( name = 'service', value = 'OGC Service type', allowableValues = "[WMTS]", defaultValue = 'WMTS', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.0.0]", defaultValue = '1.0.0', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[GetCapabilities, GetTile]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'format', value = 'MIME type of result image', defaultValue = "image/jpeg", allowableValues = "[image/jpeg, image/png]", paramType = 'query', dataType = 'string', required = false ),
            @ApiImplicitParam( name = 'tileRow', value = 'Tile row', defaultValue = "0", paramType = 'query', dataType = 'integer', required = false ),
            @ApiImplicitParam( name = 'tileCol', value = 'Tile column', defaultValue = "0", paramType = 'query', dataType = 'integer', required = false ),
            @ApiImplicitParam( name = 'tileMatrix', value = 'Tile matrix', defaultValue = "0", allowableValues = "[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]", paramType = 'query', dataType = 'string', required = false ),
            @ApiImplicitParam( name = 'tileMatrixSet', value = 'Tile matrix set', defaultValue = "WorldGeographic", paramType = 'query', dataType = 'string', required = false ),
    ] )
    def index() {
        def jsonData = request.JSON ? request.JSON as HashMap : null
        def requestParams = params - params.subMap( ['controller', 'action'] )
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
          println e.message
            log.error e.message.toString()
            response.status = 400
            render e.toString()
          //  render contentType: 'application/xml', text: exceptionService.createMessage( e.message )
        }

    }
//    @Secured( ['IS_AUTHENTICATED_ANONYMOUSLY'] )
    @ApiOperation( value = "Get the capabilities of the server", produces = 'application/vnd.ogc.wms_xml',
            notes=""" Will return the capabilities of the server.
<br>
<br>
    <ul>
        <li>
            <b>service</b><p/>
            Is a required parameter that has a fixed value of WMTS
        </li>
        <br><br>
        <li>
            <b>version</b><p/>
            Version 1.0.0 is supported
        </li>
        <br><br>
        <li>
            <b>request</b><p/>
            This is special method and only services GetCapabilities.
        </li>
        <br>
    </ul>
"""
    )
    @ApiImplicitParams( [
            @ApiImplicitParam( name = 'service', value = 'OGC Service type', allowableValues = "[WMTS]", defaultValue = 'WMTS', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.0.0]", defaultValue = '1.0.0', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required = true ),
    ] )
    def getCapabilities()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
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
            response.status = 400
            render e.toString()
            //println "*"*40
            //e.printStackTrace()
            //println "*"*40
           // render( contentType: 'application/xml', text: exceptionService.createMessage( e.message ) )
        }
    }

//    @Secured( ['IS_AUTHENTICATED_ANONYMOUSLY'] )
    @ApiOperation( value = "Get an image tile from the server", produces = 'application/vnd.ogc.wms_xml',
            notes=""" This is the main entry point for OGC WMTS services.
<br>
<br>
    <ul>
        <li>
            <b>service</b><p/>
            Is a required parameter that has a fixed value of WMTS
        </li>
        <br><br>
        <li>
            <b>version</b><p/>
            Version 1.0.0 is supported
        </li>
        <br><br>
        <li>
            <b>request</b><p/>
            This is special method and only services GetTile.
        </li>
        <br><br>
        <li>
            <b>layer</b><p/>
            This is the layer name for the GetTile call.
        </li>
        <br><br>
        <li>
            <b>format</b><p/>
            Defines the output format of the tile if the request is GetTile
        </li>
        <br><br>
        <li>
            <b>tileRow</b><p/>
            Tile numbering starts in the upper left corner of a pyramid.  So tileRow = 0 refers to the top row of
            tiles.  This is the tileRow index for the tileMatrix.
        </li>
        <br><br>
        <li>
            <b>tileCol</b><p/>
            Tile numbering starts in the upper left corner of a pyramid.  So tileCol = 0 refers to the first column of
            tiles.  This is the tileCol index for the tileMatrix.
        </li>
        <br><br>
        <li>
            <b>tileMatrix</b><p/>
            Character String type, not empty.  This is string identifier.  On our server the capabilities will list this as
            identifiers from 0 .. maxLeve.  This will have a more 1 to 1 mapping to the level/row/col format where the tileMatrix id refers
            to the level and level 0 is the coarsest resolution down to the highest resolution of maxLevel.
        </li>
        <br><br>
        <li>
            <b>tileMatrixSet</b><p/>
            Character String type, not empty. This is the Set idientifier.  This can be read from the capabilites.  By default we
            have the WorldGeographic is a default MatrixSet identifier
        </li>
        <br>
    </ul>
"""
    )
    @ApiImplicitParams( [
            @ApiImplicitParam( name = 'service', value = 'OGC service type', allowableValues = "[WMTS]", defaultValue = 'WMTS', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.0.0]", defaultValue = '1.0.0', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[GetTile]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'layer', value = 'Layer name', defaultValue = "WorldGeographic", paramType = 'query', dataType = 'integer', required = true ),
            @ApiImplicitParam( name = 'format', value = 'MIME type of result image', defaultValue = "image/jpeg", allowableValues = "[image/jpeg, image/png]", paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'tileRow', value = 'Tile row', defaultValue = "0", paramType = 'query', dataType = 'integer', required = true ),
            @ApiImplicitParam( name = 'tileCol', value = 'Tile column', defaultValue = "0", paramType = 'query', dataType = 'integer', required = true ),
            @ApiImplicitParam( name = 'tileMatrix', value = 'Tile matrix', defaultValue = "0", allowableValues = "[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]", paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'tileMatrixSet', value = 'Tile matrix set', defaultValue = "WorldGeographic", paramType = 'query', dataType = 'string', required = true ),
    ] )
    def getTile()
    {
        def jsonData = request.JSON ? request.JSON as HashMap : null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new GetTileCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( GetTileCommand, requestParams )
        bindData( cmd, requestParams )


        //println cmd

        // Getting the outputStream then testing in the finally will get rid
        // of the exceptions that state:
        //    Caused by: org.grails.gsp.GroovyPagesException: Error processing GroovyPageView: getOutputStream() has already been called for this response
        // After putting in the finally and the try catches you see the message is now trapped
        //
        def outputStream = null
        try
        {
            outputStream = response.outputStream
            def result = webMapTileService.getTile( cmd )

            if(result.contentType) response.contentType = result.contentType
            if(result.data?.length) response.contentLength = result.data.length
            response.status = result.status
            if(outputStream)
            {
                outputStream << result.data
            }
        }
        catch ( e )
        {
            log.debug(e.toString())
            println e.message
        }
        finally{
            if(outputStream!=null)
            {
                try{
                    outputStream.close()
                }
                catch(e)
                {
                    log.debug(e.toString())
                    println e.message
                }
            }
        }
    }
    @ApiOperation( value = "Get the layers defined in the WMTS service", produces = 'application/json',
            notes="""
This is a simple service call that simplifies the layer listing to just dump the wmts layer table
to a json formatted result
<br>
"""
    )
    @ApiImplicitParams( [
    ] )
    def layers(){
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new GetLayersCommand()

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( GetLayersCommand, requestParams )
        bindData( cmd, requestParams )
        HashMap result = webMapTileService.getLayers(cmd)

        response.status = result.statusCode
        render contentType: "application/json", text: result as JSON
    }
}
