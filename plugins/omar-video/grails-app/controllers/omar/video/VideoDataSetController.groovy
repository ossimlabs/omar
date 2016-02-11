package omar.video

import omar.core.HttpStatusMessage

class VideoDataSetController
{
    def videoDataSetService

    def addVideo()
    {
        def httpStatusMessage = new HttpStatusMessage()
        def status = videoDataSetService.addVideo( httpStatusMessage, params )

        response.status = httpStatusMessage.status
        render( httpStatusMessage.message )
    }

    def removeVideo()
    {
        def httpStatusMessage = new HttpStatusMessage()
        def status = videoDataSetService.removeVideo( httpStatusMessage, params )

        response.status = httpStatusMessage.status
        render( httpStatusMessage.message )
    }
}
