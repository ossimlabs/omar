package ossimtools.app

class OssimToolsController
{

  def ossimToolsService

  def index()
  {
    def lat = params.double( 'lat' ) ?: 48.48
    def lon = params.double( 'lon' ) ?: -113.79
    def radiusROI = params.double( 'radiusROI' ) ?: 8000
    def radiusLZ = params.double( 'radiusLZ' ) ?: 100
    def roughness = params.double( 'roughness' ) ?: 0.45
    def slope = params.double( 'slope' ) ?: 7.0
    def fovStart = params.double( 'fovStart' ) ?: 0.0
    def fovStop = params.double( 'fovStop' ) ?: 360.0
    def gainFactor = params.double( 'gainFactor' ) ?: 2.0
    def sunAz = params.double( 'sunAz' ) ?: 135.0
    def sunEl = params.double( 'sunEl' ) ?: 45.0
    def heightOfEye = params.double( 'heightOfEye' ) ?: 1.5

    def initParams = [
        name: 'unassigned',
        lat: lat,
        lon: lon,
        radius: radiusROI,
        rlz: radiusLZ,
        roughness: roughness,
        slope: slope,
        fovStart: fovStart,
        fovStop: fovStop,
        heightOfEye: heightOfEye,
        gainFactor: gainFactor,
        sunAz: sunAz,
        sunEl: sunEl
    ]

    [initParams: initParams]
  }

  def renderHLZ()
  {
    params.name = 'hlz'
    def results = OssimToolsService.execTool( params )
    render contentType: results.contentType, file: results.buffer
   }

  def renderViewshed()
  {
    params.name = 'viewshed'
    println params
    def results = OssimToolsService.execTool( params )
    render contentType: results.contentType, file: results.buffer
  }

  def renderSlope()
  {
    params.name = 'slope'
    def results = OssimToolsService.execTool( params )
    render contentType: results.contentType, file: results.buffer
  }
  
  def renderHillShade()
  {
    params.name = 'hillshade'
    def results = OssimToolsService.execTool( params )
    render contentType: results.contentType, file: results.buffer
  }
}
