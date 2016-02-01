package omar.wms

class WmsController
{
  def webMappingService

  def index()
  {
    def wmsParams = params - params.subMap( ['controller', 'format'] )
    def op = wmsParams.find { it.key.equalsIgnoreCase( 'request' ) }

    //println wmsParams

    switch ( op?.value?.toUpperCase() )
    {
    case "GETCAPABILITIES":
      forward action: 'getCapabilities'
      break
    case "GETMAP":
      forward action: 'getMap'
      break
    }
  }

  def getCapabilities(GetCapabilitiesRequest wmsParams)
  {
    def results = webMappingService.getCapabilities( wmsParams )

    render contentType: results.contentType, text: results.buffer

  }

  def getMap(GetMapRequest wmsParams)
  {
    def results = webMappingService.getMap( wmsParams )

    render contentType: results.contentType, file: results.buffer
  }
}
