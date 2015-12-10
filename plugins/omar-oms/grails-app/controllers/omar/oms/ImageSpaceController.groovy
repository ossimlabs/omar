package omar.oms

import groovy.json.JsonBuilder

class ImageSpaceController
{
  def imageSpaceService

  def index(GetTileCommand cmd)
  {

    def filename = cmd.filename ?: '/data/bmng/world.200406.A1.tif'
    def entry = cmd.entry ?: 0

    def imageInfo = imageSpaceService.readImageInfo( filename as File )

//    println new JsonBuilder( imageInfo ).toString()
//    println imageInfo.images[entry]

    def imageModel = [
        filename: filename,
        entry: entry,
        imageWidth: imageInfo.images[entry].resLevels[0].width,
        imageHeight: imageInfo.images[entry].resLevels[0].height,
        start: 0,
        stop: imageSpaceService.findIndexOffset( imageInfo.images[0] )
    ]

    [imageModel: imageModel]
  }

  def getTileOverlay(GetTileCommand cmd)
  {
    // println params
    // println cmd

    def results = imageSpaceService.getTileOverlay( cmd )

    render contentType: results.contentType, file: results.buffer
  }

  def getTile(GetTileCommand cmd)
  {
    //println params
    println cmd

    def results = imageSpaceService.getTile( cmd )

    render contentType: results.contentType, file: results.buffer
  }
}
