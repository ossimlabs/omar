package omar.wfs

class WfsController
{
  def webFeatureService

  def index()
  {
    def wfsParams = params - params.subMap( ['controller', 'format'] )
    def op = wfsParams.find { it.key.equalsIgnoreCase( 'request' ) }

    //println wfsParams

    switch ( op?.value?.toUpperCase() )
    {
    case "GETCAPABILITIES":
      forward action: 'getCapabilities'
      break
    case "DESCRIBEFEATURETYPE":
      forward action: 'describeFeatureType'
      break
    case "GETFEATURE":
      forward action: 'getFeature'
      break
    }
  }

  def getCapabilities(GetCapabilitiesRequest wfsParams)
  {
    def results = webFeatureService.getCapabilities( wfsParams )

    render contentType: results.contentType, text: results.buffer
  }

  def describeFeatureType(DescribeFeatureTypeRequest wfsParams)
  {
    def results = webFeatureService.describeFeatureType( wfsParams )

    render contentType: results.contentType, text: results.buffer
  }

  def getFeature(GetFeatureRequest wfsParams)
  {
    def results = webFeatureService.getFeature( wfsParams )

    render contentType: results.contentType, text: results.buffer
  }
}
