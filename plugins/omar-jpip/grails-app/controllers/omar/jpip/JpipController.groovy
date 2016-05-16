package omar.jpip

import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import grails.converters.JSON
import omar.core.BindUtil

class JpipController {

    def jpipService

    def index() {
        render ""

    }

    @ApiOperation( value = "Query the JPIP server stream link", produces = 'application/json',
            notes="""
<br>
<br>
    <ul>
        <li>
            <b>filename</b><p/>
            This is the filename of the image you wish to have a JPIP stream created for.  If one is already
            created then it will return a URL link with the value of FINISHED
        </li>
        <br><br>
        <li>
            <b>entry</b><p/>
            For multi entry Images you need to specify the entry number.   We should default to entry 0
        </li>
        <br>
    </ul>
"""
    )
    @ApiImplicitParams( [
            @ApiImplicitParam( name = 'filename', value = 'Filename to request a stream for', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'entry', value = 'Entry to request', defaultValue = '0', paramType = 'query', dataType = 'string', required = true ),
    ] )
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
