package hlz.app

class HlzController
{

  def hlzService

  def index()
  {
    def lat = params.double( 'lat' ) ?: 48.48
    def lon = params.double( 'lon' ) ?: -113.79
    def radiusROI = params.double( 'radiusROI' ) ?: 8000
    def radiusLZ = params.double( 'radiusLZ' ) ?: 100
//    def roughness = params.double( 'roughness' ) ?: 0.45
    def slope = params.double( 'slope' ) ?: 7.0
    def fovStart = params.double( 'fovStart' ) ?: 0.0
    def fovStop = params.double( 'fovStop' ) ?: 360.0
    def heightOfEye = params.double( 'heightOfEye' ) ?: 1.5

    def extent = hlzService.calculateExtent( lat, lon, radiusROI )

    def initParams = [
        lat: lat,
        lon: lon,
        radiusROI: radiusROI,
        radiusLZ: radiusLZ,
//        roughness: roughness,
        slope: slope,
        fovStart: fovStart,
        fovStop: fovStop,
        heightOfEye: heightOfEye,
        extent: extent
    ]

    [initParams: initParams]
  }

  def renderHLZ()
  {
    def results = hlzService.renderHLZ( params )

    render contentType: results.contentType, file: results.buffer
  }

  def renderVS()
  {
    println params
    def results = hlzService.renderVS( params )

    render contentType: results.contentType, file: results.buffer
  }

  def renderHillShade()
  {
    def results = hlzService.renderHillShade( params )

    render contentType: results.contentType, file: results.buffer
  }
}
