package omar.video

import grails.transaction.Transactional
import org.apache.commons.io.FilenameUtils

@Transactional( readOnly = true )
class VideoStreamingService
{
	def grailsLinkGenerator
	def grailsApplication

	def getVideoDetails( def params )
	{
		def flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
		def flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    def videoURL = null

		def videoId = (params.id ==~ /\d+/ ) ? params.id as Long : null

		def videoDataSet = VideoDataSet.where {
			id == videoId || indexId == params?.id
		}.get()

		println videoDataSet.filename

		if ( videoDataSet )
		{
			def videoFile = videoDataSet.filename as File
			def flvFile = "${ flashDirRoot }/${ FilenameUtils.getBaseName( videoFile.name ) }.flv" as File

			videoURL = grailsLinkGenerator.link( absolute: true, base: flashUrlRoot, uri: "/${ flvFile.name }" )

			if ( !flvFile.exists() )
			{
				convertVideo( videoFile, flvFile )
			}
		}

		[ videoDataSet: videoDataSet, videoURL: videoURL ]
	}

	private static def convertVideo( File videoFile, File flvFile )
	{
		def cmd = [
				"ffmpeg",
				"-i",
				"${ videoFile.absolutePath }",
				"-an",
				"-vb",
				"2048k",
				"-r",
				"15",
				"-y",
				"${ flvFile.absolutePath }"
		]

		println cmd.join( ' ' )

		def start = System.currentTimeMillis()
		def proc = cmd.execute()

		proc.consumeProcessOutput()

		def exitCode = proc.waitFor()
		def stop = System.currentTimeMillis()

		println "elapsed: ${ stop - start }ms"
		println "exitCode: ${ exitCode }"
	}
}
