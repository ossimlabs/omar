package omar.wfs

import omar.core.BindUtil
import com.github.rahulsom.swaggydoc.*
import com.wordnik.swagger.annotations.*

@Api(value = "wfs",
        description = "WFS Support"
)
class WfsController
{
  def webFeatureService

  static defaultAction = "index"


  def index()
  {
    def wfsParams = params - params.subMap( ['controller', 'format', 'action'] )
    def op = wfsParams.find { it.key.equalsIgnoreCase( 'request' ) }

    switch ( request?.method?.toUpperCase() )
    {
    case 'GET':
      op = wfsParams.find { it.key.equalsIgnoreCase( 'request' ) }?.value
      break
    case 'POST':
      op = request?.XML?.name()
      break
    }

    def results

//    println wfsParams

//    println '*' * 40
//    println op

    switch ( op?.toUpperCase() )
    {
    case "GETCAPABILITIES":
//      println 'GETCAPABILITIES'
//      forward action: 'getCapabilities'

      def cmd = new GetCapabilitiesRequest()

      switch ( request?.method?.toUpperCase() )
      {
      case 'GET':
        BindUtil.fixParamNames( GetCapabilitiesRequest, wfsParams )
        bindData( cmd, wfsParams )
        break
      case 'POST':
        cmd = cmd.fromXML( request.XML )
        break
      }

      results = webFeatureService.getCapabilities( cmd )
      break
    case "DESCRIBEFEATURETYPE":
//      println 'DESCRIBEFEATURETYPE'
//      forward action: 'describeFeatureType'

      def cmd = new DescribeFeatureTypeRequest()

      switch ( request?.method?.toUpperCase() )
      {
      case 'GET':
        BindUtil.fixParamNames( DescribeFeatureTypeRequest, wfsParams )
        bindData( cmd, wfsParams )
        break
      case 'POST':
        cmd = cmd.fromXML( request.XML )
        break
      }

      results = webFeatureService.describeFeatureType( cmd )
      break
    case "GETFEATURE":
//      println 'GETFEATURE'
//      forward action: 'getFeature'

      def cmd = new GetFeatureRequest()

      switch ( request?.method?.toUpperCase() )
      {
      case 'GET':
        BindUtil.fixParamNames( GetFeatureRequest, wfsParams )
        bindData( cmd, wfsParams )
        break
      case 'POST':
        cmd = cmd.fromXML( request.XML )
        break
      }

      results = webFeatureService.getFeature( cmd )
      break
    default:
      println 'UNKNOWN'
      break

    }

//    println '*' * 40

    render contentType: results.contentType, text: results.buffer
  }

  @ApiOperation(value = "Get the capabilities of the server", produces='application/xml')
  @ApiImplicitParams([
          @ApiImplicitParam(name = 'service', value = 'OGC Service type', allowableValues="[WFS]", defaultValue = 'WFS', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'version', value = 'Version to request', allowableValues="[1.1.0]", defaultValue = '1.1.0', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'request', value = 'Request type', allowableValues="[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required=true),
  ])
  def getCapabilities(/*GetCapabilitiesRequest wfsParams*/)
  {
    def wfsParams = new GetCapabilitiesRequest()

    BindUtil.fixParamNames( GetCapabilitiesRequest, params )
    bindData( wfsParams, params )

    def results = webFeatureService.getCapabilities( wfsParams )

    render contentType: results.contentType, text: results.buffer
  }

  @ApiOperation(value = "Describe the feature from the server", produces='application/xml')
  @ApiImplicitParams([
          @ApiImplicitParam(name = 'service', value = 'OGC Service type', allowableValues="[WFS]", defaultValue = 'WFS', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'version', value = 'Version to request', allowableValues="[1.1.0]", defaultValue = '1.1.0', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'request', value = 'Request type', allowableValues="[DescribeFeatureType]", defaultValue = 'DescribeFeatureType', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'typeName', value = 'Type Name', defaultValue="omar:raster_entry", paramType = 'query', dataType = 'string', required=true)
  ])
  def describeFeatureType(/*DescribeFeatureTypeRequest wfsParams*/)
  {
    def wfsParams = new DescribeFeatureTypeRequest()

    BindUtil.fixParamNames( DescribeFeatureTypeRequest, params )
    bindData( wfsParams, params )
    def results = webFeatureService.describeFeatureType( wfsParams )

    render contentType: results.contentType, text: results.buffer
  }

  @ApiOperation(value = "Get features from the server", produces='application/xml,application/json')
  @ApiImplicitParams([
          @ApiImplicitParam(name = 'service', value = 'OGC service type', allowableValues="[WFS]", defaultValue = 'WFS', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'version', value = 'Version to request', allowableValues="[1.1.0]", defaultValue = '1.1.0', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'request', value = 'Request type', allowableValues="[GetFeature]", defaultValue = 'GetFeature', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'typeName', value = 'Type name', defaultValue="omar:raster_entry", paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'filter', value = 'Filter', paramType = 'query', dataType = 'string', required=false),
          @ApiImplicitParam(name = 'resultType', value = 'Result type', defaultValue="results", allowableValues="[results,hits]", paramType = 'query', dataType = 'string', required=false),
          @ApiImplicitParam(name = 'outputFormat', value = 'Output format', defaultValue="JSON", allowableValues="[JSON, GML2, GML3, GML32]", paramType = 'query', dataType = 'string', required=false),
          @ApiImplicitParam(name = 'sortBy', value = 'Sort by', paramType = 'query', dataType = 'string'),
          @ApiImplicitParam(name = 'propertyName', value = 'Property name (comma separated fields)', defaultValue="", paramType = 'query', dataType = 'string', required=false),
          @ApiImplicitParam(name = 'maxFeatures', value = 'Maximum Features in the result', defaultValue="10", paramType = 'query', dataType = 'int', required=false),
          @ApiImplicitParam(name = 'startIndex', value = 'Starting offset', defaultValue="0", paramType = 'query', dataType = 'int', required=false),
  ])
  def getFeature(/*GetFeatureRequest wfsParams*/)
  {
//    println wfsParams
    def wfsParams = new GetFeatureRequest()

    BindUtil.fixParamNames( GetFeatureRequest, params )
    bindData( wfsParams, params )

    def results = webFeatureService.getFeature( wfsParams )

    render contentType: results.contentType, text: results.buffer
  }
}
