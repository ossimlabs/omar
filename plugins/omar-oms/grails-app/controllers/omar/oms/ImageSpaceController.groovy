package omar.oms

class ImageSpaceController
{
  def imageSpaceService

  def index(GetTileCommand cmd)
  {
    def filename = cmd.filename ?: '/data/uav/predator/vesdata/po_197675_pan_0000000.ntf'
    def entry = cmd.entry ?: 0
    def imageInfo = imageSpaceService.readImageInfo( filename as File )

//    println imageInfo

    [
        filename: filename,
        entry: entry,
        imgWidth: imageInfo.images[entry].resLevels[0].width,
        imgHeight: imageInfo.images[entry].resLevels[0].height
    ]
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
}
