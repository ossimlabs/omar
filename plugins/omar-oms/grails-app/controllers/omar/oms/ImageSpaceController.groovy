package omar.oms

class ImageSpaceController {
    def imageSpaceService

    def index()
    {
        def omsImage = new OmsImage()

        def filename = params.filename ?: '/data/bmng/world.200406.A1.tif'
        def imageInfo = omsImage.readImageInfo( filename as File )

        println imageInfo

        def imageModel = [
            filename: filename,
            imageWidth: imageInfo.width,
            imageHeight: imageInfo.height,
            start: 0,
            stop: omsImage.findIndexOffset( imageInfo )
        ]

        [imageModel: imageModel]
    }

    def getTileOverlay(GetTileCommand cmd)
    {
        //println params
//    println cmd

        def results = imageSpaceService.getTileOverlay( cmd )

        render contentType: results.contentType, file: results.buffer
    }

    def getTile(GetTileCommand cmd)
    {
        //println params
        println cmd

        def results = imageSpaceService.getTile( cmd )

        render contentType: results.contentType, file: results.buffer
    }}
