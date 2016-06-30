package omar.wcs

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import omar.core.BindUtil

@Api( value = "wcs",
    description = "WCS Support"
)
class WcsController
{
  def webCoverageService
  def proxyWebCoverageService

  def index()
  {
//    println "index: ${params} ${request.method}"

    def wcsParams = params - params.subMap( ['controller', 'format'] )
    def operation = wcsParams.find { it.key.equalsIgnoreCase( 'request' ) }

    switch ( operation?.value?.toUpperCase() )
    {
    case "GETCAPABILITIES":
//      println "GETCAPABILITIES"
      forward action: 'getCapabilities'
      break
    case "DESCRIBECOVERAGE":
//      println "DESCRIBECOVERAGE"
      forward action: 'describeCoverage'
      break
    case "GETCOVERAGE":
//      println "GETCOVERAGE"
      forward action: 'getCoverage'
      break
//    default:
//      println "default"
//      def results = proxyWebCoverageService.getCapabilities( params )
//
//      render contentType: results.contentType, text: results.buffer
    }
  }

  @ApiOperation( value = "Get the capabilities of the server", produces = 'application/xml' )
  @ApiImplicitParams( [
      @ApiImplicitParam( name = 'service', value = 'OGC Service type', allowableValues = "[WCS]", defaultValue = 'WCS', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.0.0]", defaultValue = '1.0.0', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[GetCapabilities]", defaultValue = 'GetCapabilities', paramType = 'query', dataType = 'string', required = true ),

      @ApiImplicitParam( name = 'coverage', value = 'Coverage', paramType = 'query', dataType = 'string', required = false ),
      @ApiImplicitParam( name = 'filter', value = 'Filter', paramType = 'query', dataType = 'string', required = false )

  ] )
  def getCapabilities(GetCapabilitiesRequest wcsParams)
  {
    println params
//    println "getCapabilities: ${params}"
    BindUtil.fixParamNames( GetCapabilitiesRequest, params )
    bindData( wcsParams, params )

    def results = webCoverageService.getCapabilities( wcsParams )

    render contentType: results.contentType, text: results.buffer
  }

  @ApiOperation( value = "Get the description of coverage from the server", produces = 'application/xml' )
  @ApiImplicitParams( [
      @ApiImplicitParam( name = 'service', value = 'OGC Service type', allowableValues = "[WCS]", defaultValue = 'WCS', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.0.0]", defaultValue = '1.0.0', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[DescribeCoverage]", defaultValue = 'DescribeCoverage', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'coverage', value = 'Coverage', paramType = 'query', dataType = 'string', required = false ),
      @ApiImplicitParam( name = 'filter', value = 'Filter', paramType = 'query', dataType = 'string', required = false )

  ] )
  def describeCoverage(DescribeCoverageRequest wcsParams)
  {
    println params

//    println "describeCoverage: ${params}"
    BindUtil.fixParamNames( DescribeCoverageRequest, params )
    bindData( wcsParams, params )

    def results = webCoverageService.describeCoverage( wcsParams )

    render contentType: results.contentType, text: results.buffer
  }

  @ApiOperation( value = "Get image from the server", produces = 'image/tiff,application/xml,application/json' )
  @ApiImplicitParams( [
      @ApiImplicitParam( name = 'service', value = 'OGC service type', allowableValues = "[WCS]", defaultValue = 'WCS', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'version', value = 'Version to request', allowableValues = "[1.0.0]", defaultValue = '1.0.0', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'request', value = 'Request type', allowableValues = "[GetCoverage]", defaultValue = 'GetCoverage', paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'coverage', value = 'Type name', defaultValue = "omar:raster_entry", paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'filter', value = 'Filter', paramType = 'query', dataType = 'string', required = false ),
      @ApiImplicitParam( name = 'crs', value = 'Coordinate Reference System', defaultValue = "epsg:4326", paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'bbox', value = 'Bounding box', defaultValue = "-180,-90,180,90", paramType = 'query', dataType = 'string', required = true ),
      @ApiImplicitParam( name = 'width', value = 'Width of result image', defaultValue = "1024", paramType = 'query', dataType = 'int', required = true ),
      @ApiImplicitParam( name = 'height', value = 'Height of result image', defaultValue = "512", paramType = 'query', dataType = 'int', required = true ),
      @ApiImplicitParam( name = 'format', value = 'Format Type of result image', defaultValue = "GeoTIFF", allowableValues = "[GeoTIFF]", paramType = 'query', dataType = 'string', required = true )
  ] )
  def getCoverage(GetCoverageRequest wcsParams)
  {
    println params

//    println "getCoverage: ${params}"
    BindUtil.fixParamNames( GetCoverageRequest, params )
    bindData( wcsParams, params )

    def results = webCoverageService.getCoverage( wcsParams )

    render contentType: results.contentType, file: results.buffer
  }
}
