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

    String wfsUrl
    String dateRanges
    String locationFields
    String categoryFields
    String dateField
    String expirePeriod
    @ApiOperation(value = "Index data via WMS into PredictionIO database", produces='application/json', httpMethod = "POST")
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'wfsUrl', value = "Base WFS url override", defaultValue = '', required=false, paramType = 'query', dataType = 'string'),
            @ApiImplicitParam(name = 'dateRanges', value = 'Date ranges to add', defaultValue = '', paramType = 'query', required=false, dataType = 'string'),
            @ApiImplicitParam(name = 'locationFields', value = 'WFS Location fields', defaultValue = 'country_code,be_number', paramType = 'query', dataType = 'string'),
            @ApiImplicitParam(name = 'categoryFields', value = 'WFS Category fields', defaultValue = 'mission_id,image_category,sensor_id', paramType = 'query', dataType = 'string'),
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
