package omar.mensa

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import omar.oms.IptsToGrdCommand
import omar.oms.ImageGeometryService
import grails.converters.JSON
import omar.core.BindUtil

@Api(value = "mensa",
        description = "API operations for Mensuration",
        produces = 'application/json',
        consumes = 'application/json'
)
class MensaController {

    def imageGeometryService
    def mensaService
    static allowedMethods = [
            index: ['GET', 'POST'],
            imageDistance: 'POST',
            iptsToGrd: 'POST'
    ]

    def index() { }
    @ApiOperation(value = "Compute distance using a WKT format polygon in image space",
            consumes= 'application/json',
            produces='application/json', httpMethod="POST")
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'body',
                    value = "General Message for querying recommendations",
                    defaultValue = """{
   "filename": "<Path to File>",
   "entryId": 0,
   "wkt": ""
    }""",
                    paramType = 'body',
                    dataType = 'string')
    ])
    def imageDistance()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new DistanceCommand()
        if(jsonData) requestParams << jsonData
        println jsonData
        println "*"*40

        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( DistanceCommand, requestParams )
        bindData( cmd, requestParams )
        HashMap result = mensaService.calculateImageDistance(cmd)

        response.status = result.status
        render contentType: "application/json", text: result as JSON
    }

    @ApiOperation(value = "Convert Image Points to Ground coordinates",
                  consumes= 'application/json',
                  produces='application/json', httpMethod="POST")
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'body',
                    value = "General Message for querying recommendations",
                    defaultValue = """{
   "filename": "<Path to File>",
   "entryId": 0,
   "includePositionError":false,
   "ipts": [
           {"x":0.0,"y":0.0},
           {"x":1.0,"y":1.0}
           ]
    }""",
                   paramType = 'body',
                   dataType = 'string')
    ])
    def iptsToGrd()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IptsToGrdCommand()
        if(jsonData) requestParams << jsonData
        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IptsToGrdCommand, requestParams )
        bindData( cmd, requestParams )
        HashMap result = imageGeometryService.iptsToGrd(cmd)

        response.status = result.status
        render contentType: "application/json", text: result as JSON
    }
}
