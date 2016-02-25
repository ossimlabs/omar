package omar.predio.index

import com.github.rahulsom.swaggydoc.*
import com.wordnik.swagger.annotations.*
import omar.core.DateUtil
import omar.core.BindUtil
import org.joda.time.DateTimeZone

@Api(value = "PredioIndexer",
        description = """API operations for Indexing WFS data into PredictionIO database""",
        produces = 'application/json,plain/text',
        consumes = 'application/json'
)
class PredioIndexController {

    static allowedMethods = [index:["GET"],
                             indexData:["POST"]
            ]
    def predioIndexService
    def index() { }

    @ApiOperation(value = "Index data via WFS into PredictionIO database",
                  produces='application/json',
                  httpMethod = "POST",
                  notes = """
    <ul>
        <li>
            <b>wfsUrl</b><p/>
            Is an optional parameter and serves as an override.  If not specified then the default
            URL setup by the configuration of this service will be used.  If it's specified then
            the full parameters must be given for the <b>wfsUrl</b>. Example:<p>
            http://foo.example.com/ogc/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&typeName=omar:raster_entry, ....etc.
        </li>
        <br>
        <li>
            <b>dateRanges</b><p/>Is based on an ISO8601 time interval which is of the form:
<pre>
Start and end, such as:      2007-03-01T13:00:00Z/2008-05-11T15:30:00Z
Start and duration, such as: 2007-03-01T13:00:00Z/P1Y2M10DT2H30M
Duration and end, such as:   P1Y2M10DT2H30M/2008-05-11T15:30:00Z
</pre>
            </br>
            For full description of interval period format please see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
        </li>
        <br>
        <li>
        <b>locationFields</b><p/>
        Is a comma separated list of WFS feature fields that will be used for the location strings.
        </li>
        <br>
        <li>
        <b>categoryFields</b><p/>
        Is a comma separated list of WFS feature fields that will be used for the category strings.
        </li>
        <br>
        <li>
        <b>dateField</b><p/>
        Defines which feature field is used as the event date for PredictionIO.   By default the
        acquisition_date is used. If that is not found then the date will default to current date.
        </li>
        <br>
        <li>
        <b>expirePeriod</b><p/>
        Is an ISO8601 Period format that describes how long until the image expires.  For example,
        if we specify P1Y then whatever the <b>dateField</b> value is it will mark the event to expire
        after 1 year from that date.
        </li>
    </ul>
    """)
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'wfsUrl', value = "Base WFS url override", defaultValue = '', required=false, paramType = 'query', dataType = 'string'),
            @ApiImplicitParam(name = 'dateRanges', value = 'Date ranges to add', defaultValue = '', paramType = 'query', required=false, dataType = 'string'),
            @ApiImplicitParam(name = 'locationFields', value = 'WFS Location fields', defaultValue = 'country_code,be_number', allowMultiple= true, paramType = 'query', dataType = 'string'),
            @ApiImplicitParam(name = 'categoryFields', value = 'WFS Category fields', defaultValue = 'mission_id,image_category,sensor_id', allowMultiple=true, paramType = 'query', dataType = 'string'),
            @ApiImplicitParam(name = 'dateField', value = 'WFS field for date', defaultValue = 'acquisition_date', paramType = 'query', dataType = 'string'),
            @ApiImplicitParam(name = 'expirePeriod', value = 'ISO8601 Period', defaultValue = 'P3D', paramType = 'query', dataType = 'string'),
    ])
    def indexData()
    {
        HashMap result
        if(OmarPredioIndexUtils.predioIndexConfig.enabled)
        {
            // bind with JSON or URL params
            def jsonData = request.JSON?request.JSON as HashMap:null
            def requestParams = params - params.subMap( ['controller', 'format', 'action'] )
            def cmd = new PredioIndexDataCommand()
            if(jsonData) requestParams << jsonData
            BindUtil.fixParamNames( PredioIndexDataCommand, requestParams )
            bindData( cmd, requestParams )

            result = predioIndexService.indexData(cmd)
        }
        else
        {
            result = [status : HttpStatus.BAD_REQUEST,
                      message: "Indexing is disabled",
                      contentType:"text/plain"]

        }

        response.status      = result.status.value()
        response.contentType = result.contentType
        render result.message
    }
}
