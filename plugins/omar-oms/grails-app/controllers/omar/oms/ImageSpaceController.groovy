package omar.oms

import grails.converters.JSON
import com.github.rahulsom.swaggydoc.*
import com.wordnik.swagger.annotations.*
import omar.core.BindUtil

@Api(value = "imageSpace",
        description = "API operations in image space."
)
class ImageSpaceController
{
  def imageSpaceService

  def index(/*GetTileCommand cmd*/)
  {
    def cmd = new GetTileCommand()

    BindUtil.fixParamNames( GetTileCommand, params )
    bindData( cmd, params )
    def filename = cmd.filename //?: '/data/uav/predator/vesdata/po_197675_pan_0000000.ntf'
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

  @ApiOperation(value = "Get a tile from the passed in image file", produces="image/jpeg,image/png,image/gif")
  @ApiImplicitParams([
          @ApiImplicitParam(name = 'x', value = 'Tile in x direciton', defaultValue = '0', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'y', value = 'Tile in y direction', defaultValue = '0', paramType = 'query', dataType = 'int', required=true),
          @ApiImplicitParam(name = 'z', value = 'Resolution level (0 full resolution)', defaultValue = '0', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'tileSize', value = 'Tile size', allowableValues="[512,256]", defaultValue="256", paramType = 'query', dataType = 'int', required=true),
          @ApiImplicitParam(name = 'format', value = 'Output image format', allowableValues="[png,jpeg,gif]", defaultValue="png", paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'filename', value = 'Filename', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'entry', value = 'Image entry id(typically 0)', defaultValue="0", paramType = 'query', dataType = 'string', required=true),
  ])
  def getTile(/*GetTileCommand cmd*/)
  {
     def cmd = new GetTileCommand()

     BindUtil.fixParamNames( GetTileCommand, params )
     bindData( cmd, params )
    def results = imageSpaceService.getTile( cmd )

    render contentType: results.contentType, file: results.buffer
  }


  @ApiOperation(value = "Get the footprint of  tile and its name", produces="image/jpeg,image/png,image/gif")
  @ApiImplicitParams([
          @ApiImplicitParam(name = 'x', value = 'Tile in x direciton', defaultValue = '0', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'y', value = 'Tile in y direction', defaultValue = '0', paramType = 'query', dataType = 'int', required=true),
          @ApiImplicitParam(name = 'z', value = 'Resolution level (0 full resolution)', defaultValue = '0', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'tileSize', value = 'Tile size', allowableValues="[512,256]", defaultValue="256", paramType = 'query', dataType = 'int', required=true),
          @ApiImplicitParam(name = 'format', value = 'Output image format', allowableValues="[png,jpeg,gif]", defaultValue="png", paramType = 'query', dataType = 'string', required=true),
  ])
  def getTileOverlay(/*GetTileCommand cmd*/)
  {
//    println params

    def cmd = new GetTileCommand()

    BindUtil.fixParamNames( GetTileCommand, params )
    bindData( cmd, params )
//    println cmd

    def results = imageSpaceService.getTileOverlay( cmd )

    render contentType: results.contentType, file: results.buffer
  }

  def getAngles()
  {
    //println params

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

  @ApiOperation(value = "Get the thumbnail of the passed in file name", produces="image/jpeg,image/png,image/gif")
  @ApiImplicitParams([
          @ApiImplicitParam(name = 'size', value = 'Overview image size', allowableValues="[64,128,256]", defaultValue="256", paramType = 'query', dataType = 'int', required=true),
          @ApiImplicitParam(name = 'format', value = 'Output image format', allowableValues="[png,jpeg,gif]", defaultValue="png", paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'filename', value = 'Filename', paramType = 'query', dataType = 'string', required=true),
          @ApiImplicitParam(name = 'entry', value = 'Image entry id(typically 0)', defaultValue="0", paramType = 'query', dataType = 'string', required=true),
  ])
  def getThumbnail(/*GetThumbnailCommand cmd*/)
  {
     def cmd = new GetThumbnailCommand()

     BindUtil.fixParamNames( GetTileCommand, params )
     bindData( cmd, params )

     def results = imageSpaceService.getThumbnail( cmd )

    render contentType: results.contentType, file: results.buffer
  }
}
