package omar.video

class VideoStreamingController
{
	def videoStreamingService

	def index()
	{
		def videoDetails = videoStreamingService.getVideoDetails(params)

    videoDetails
	}
}
