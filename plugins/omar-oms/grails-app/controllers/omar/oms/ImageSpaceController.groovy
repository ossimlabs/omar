package omar.oms

import grails.converters.JSON

class ImageSpaceController
{
  def imageSpaceService

  def index(GetTileCommand cmd)
  {
    def filename = cmd.filename ?: '/data/uav/predator/vesdata/po_197675_pan_0000000.ntf'
    def entry = cmd.entry ?: 0
    def imageInfo = imageSpaceService.readImageInfo( filename as File )
    def upAngle = imageSpaceService.computeUpIsUp( filename, entry )
    def northAngle = imageSpaceService.computeNorthIsUp( filename, entry )

//    println imageInfo

    def initParams = [
        filename: filename,
        entry: entry,
        imgWidth: imageInfo.images[entry].resLevels[0].width,
        imgHeight: imageInfo.images[entry].resLevels[0].height,
        upAngle: Math.toRadians( upAngle ),
        northAngle: Math.toRadians( northAngle )
    ]

    [initParams: initParams]
  }

  def getTile(GetTileCommand cmd)
  {
    def results = imageSpaceService.getTile( cmd )

    render contentType: results.contentType, file: results.buffer
  }


  def getTileOverlay(GetTileCommand cmd)
  {
    def results = imageSpaceService.getTileOverlay( cmd )

    render contentType: results.contentType, file: results.buffer
  }

  def getAngles()
  {
    println params

    String filename = params.filename
    Integer entry = params.int( 'entry' )

    def upAngle = Math.toRadians( imageSpaceService.computeUpIsUp( filename, entry ) )
    def northAngle = Math.toRadians( imageSpaceService.computeNorthIsUp( filename, entry ) )

    def results = [
        upAngle: upAngle?.isNaN() ? 0 : upAngle,
        northAngle: northAngle?.isNaN() ? 0 : northAngle
    ]

    render contentType: 'application/json', text: results as JSON
  }
}
