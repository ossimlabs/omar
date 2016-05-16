package omar.jpip

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import grails.converters.JSON
import omar.core.BindUtil

@Api(value = "JPIP",
        description = "JPIP conversion is performed on the image passed in."
)
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
        <br><br>
        <li>
            <b>projCode</b><p/>
            chip=image space, geo-scaled - origin lat of true scale = image center, 4326 for geographic, 3857 for google mercator.
        </li>
        <br>
    </ul>
"""
    )
    @ApiImplicitParams( [
            @ApiImplicitParam( name = 'filename', value = 'Filename to request a stream for', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'entry', value = 'Entry to request', defaultValue = '0', paramType = 'query', dataType = 'string', required = true ),
            @ApiImplicitParam( name = 'projCode', value = 'Projection Code', allowableValues = "[chip,geo-scaled,4326,3857]", defaultValue = '4326', paramType = 'query', dataType = 'string', required = true ),
    ] )
    
    def createStream()
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

            HashMap result = jpipService.createStream(cmd)
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
}
