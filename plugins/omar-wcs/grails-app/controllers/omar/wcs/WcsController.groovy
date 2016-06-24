package omar.wcs

import omar.core.BindUtil

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

  def getCapabilities(GetCapabilitiesRequest wcsParams)
  {
//    println "getCapabilities: ${params}"
    BindUtil.fixParamNames( GetCapabilitiesRequest, params )
    bindData( wcsParams, params )

    def results = webCoverageService.getCapabilities( wcsParams )

    render contentType: results.contentType, text: results.buffer
  }

  def describeCoverage(DescribeCoverageRequest wcsParams)
  {
//    println "describeCoverage: ${params}"
    BindUtil.fixParamNames( DescribeCoverageRequest, params )
    bindData( wcsParams, params )

    def results = webCoverageService.describeCoverage( wcsParams )

    render contentType: results.contentType, text: results.buffer
  }

  def getCoverage(GetCoverageRequest wcsParams)
  {
//    println "getCoverage: ${params}"
    BindUtil.fixParamNames( GetCoverageRequest, params )
    bindData( wcsParams, params )

    def results = webCoverageService.getCoverage( wcsParams )

    render contentType: results.contentType, file: results.buffer
  }
}
