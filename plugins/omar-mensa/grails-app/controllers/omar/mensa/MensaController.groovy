package omar.mensa

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import omar.oms.GrdToIptsCommand
import omar.oms.IptsToGrdCommand
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
            imagePointsToGround: 'POST',
            groundToImagePoints: 'POST'
    ]

    def index() { }
    @ApiOperation(value = "Compute distance using a WKT format polygon in image space",
            consumes= 'application/json',
            produces='application/json', httpMethod="POST",
            notes = """
    <ul>
        <li>
            <b>filename</b><p/>
            Is the filename used for the imageToGround calculation.  This is typically a path to an image file.
        </li>
        <br>
        <li>
            <b>entryId</b><p/>Is the entry of the image.  Images can have multiple data entries and this is used to identify
            which entry we are currenlty using for this image
        </li>
        <br>
        <li>
        <b>pointList</b><p/>
        For this method we can only accept <b>pointList</b> to be of type WKT LINESTRING or POLYGON string.
        </li>
        <br>
    </ul>
""")
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'body',
                    value = "General Message for querying recommendations",
                    defaultValue = """{
   "filename": "<Path to File>",
   "entryId": 0,
   "pointList": ""
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
            produces='application/json', httpMethod="POST",
            notes = """
    <ul>
        <li>
            <b>filename</b><p/>
            Is the filename used for the imageToGround calculation.  This is typically a path to an image file.
        </li>
        <br>
        <li>
            <b>entryId</b><p/>Is the entry of the image.  Images can have multiple data entries and this is used to identify
            which entry we are currenlty using for this image
        </li>
        <br>
        <li>
        <b>pointList</b><p/>
        This can either be JSON array of image points to convert to ground lat, lon, height or could be formatted as a WKT MULTIPOINT string.

        If the service detects the input is a string and not a JSON array
        then it will try to convert as a WKT string and then grab all coordinates.
        Typical WKT string definitions would be MULTIPOINT(1 1, 2 2, ......)<br><br>
        If the list is formatted as a JSON array the service will assume that the array will have elements formatted in the form of a comma separated list of values
        [{x:,y:}, {x:,y:}
        </li>
        <br>
        <li>
        <b>pqeIncludePositionError</b><p/>
        If this value is true it will include the position quality information.  This will identify the horizontal and
        vertical error for each image point and give you the azimuth and radial distance for the semi major and semi minor values.
        </li>
        <br>
        <li>
        <b>pqeProbabilityLevel</b><p/>
        This is used to identify the probability level.  Typical values are 0.5, 0.90, and 0.95.  If the value is 0.5
        you are saying that there is a 50% confidence that the point is within the elliptical error identified by the Circular error (CE)
        and the Linear error (LE)
        </li>
        <br>
        <li>
        <b>pqeEllipsePointType</b><p/>
        This is used to allow the algorithm to calculate the points by sampling around a 360 degree
        circle defined by the semi major and minor axis.<br>
        The possible value can be <b>none</b>, <b>array</b>, <b>linestring</b>, or <b>polygon</b>. The <b>linestring</b> and <b>polygon</b> will return the points formatted in WKT
        LINESTRING or POLYGON format. If it's an <b>array</b> type it will format the values in a JSON array and each element is formatted as a JSON
        object of the form {x:,y:} object.
        </li>
        <br>
        <li>
        <li>
        <b>pqeEllipseAngularIncrement</b><p/>
        This is Used to estimate the circle and create a geometry of type defined by <b>pqeEllipsePointType</b>.
        The default estimate is a 10 degree increment.
        </li>
        <br>
        </ul>
    """)
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'body',
                    value = "General Message for querying recommendations",
                    defaultValue = """{
   "filename": "<Path to File>",
   "entryId": 0,
   "pointList": [
           {"x":0.0,"y":0.0},
           {"x":1.0,"y":1.0}
           ],
   "pqeIncludePositionError":false,
   "pqeProbabilityLevel" : 0.9,
   "pqeEllipsePointType" : "none",
   "pqeEllipseAngularIncrement": 10
}""",
                    paramType = 'body',
                    dataType = 'string')
    ])
    def imagePointsToGround()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new IptsToGrdCommand()
        if(jsonData) requestParams << jsonData
        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( IptsToGrdCommand, requestParams )
        bindData( cmd, requestParams )
        HashMap result = mensaService.imagePointsToGround(cmd)
        response.status = result.status
        render contentType: "application/json", text: result as JSON
    }

    @ApiOperation(value = "Convert Image Points to Ground coordinates",
            consumes= 'application/json',
            produces='application/json', httpMethod="POST",
            notes = """
    <ul>
        <li>
            <b>filename</b><p/>
            Is the filename used for the imageToGround calculation.  This is typically a path to an image file.
        </li>
        <br>
        <li>
            <b>entryId</b><p/>Is the entry of the image.  Images can have multiple data entries and this is used to identify
            which entry we are currenlty using for this image
        </li>
        <br>
        <li>
        <b>pointList</b><p/>
        This can either be JSON array of ground points lat, lon, and optional height or could be formatted as a WKT MULTIPOINT string.

        If the service detects the input is a string and not a JSON array
        then it will try to convert as a WKT string and then grab all coordinates.
        Typical WKT string definitions would be MULTIPOINT(1 1, 2 2, ......)<br><br>
        You can also specify the elevation in the z coordinate by doing MULTIPOINT(1 1 1, 2 2 2)<br><br>
        If the list is formatted as a JSON array the service will assume that the array will have elements formatted in the form of a comma separated list of values
        [{"lat":,"lon":,"hgt":}, {"lat":,"lon":,"hgt":}]
        </li>
        <br>
        </ul>
    """)
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'body',
                    value = "General Message for querying recommendations",
                    defaultValue = """{
   "filename": "<Path to File>",
   "entryId": 0,
   "pointList": [
           {"lat":0.0,"lon":0.0,hgt:0.0},
           {"lat":1.0,"lon":1.0}
           ],
}""",
                    paramType = 'body',
                    dataType = 'string')
    ])
    def groundToImagePoints()
    {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def requestParams = params - params.subMap( ['controller', 'action'] )
        def cmd = new GrdToIptsCommand()
        if(jsonData) requestParams << jsonData
        // get map from JSON and merge into parameters
        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( GrdToIptsCommand, requestParams )
        bindData( cmd, requestParams )
        HashMap result = mensaService.groundToImagePoints(cmd)
        response.status = result.status
        render contentType: "application/json", text: result as JSON
    }

}
