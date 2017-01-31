package omar.raster

import omar.core.Repository
import omar.core.HttpStatus

//import omar.stager.DataManagerService
import omar.stager.StagerUtil
import omar.stager.OmarStageFile
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware


class RasterDataSetService implements ApplicationContextAware {

	static transactional = true

	def parserPool
	def dataInfoService
	def ingestService
	def stagerService
	def ingestMetricsService
	ApplicationContext applicationContext


	def deleteFromRepository( Repository repository ) {
		def rasterDataSets = RasterDataSet.findAllByRepository( repository )

		rasterDataSets?.each { it.delete() }
	}

	/**
	 * This service allows one to add a raster to the omar tables.
	 *
	 * @param httpStatusMessage Is used to populate the http response.  This will
	 *                          identify the status code messages and any additional
	 *                          header paramters that need to be added to the response.
	 * @param filename is the file you wish to add to the OMAR tables
	 */
	def addRaster( def httpStatusMessage, AddRasterCommand params ) {
		String filename = params?.filename
		httpStatusMessage?.status = HttpStatus.OK
		httpStatusMessage?.message = "Added raster ${filename}"
		URI uri = new URI(filename.toString())
		String scheme = uri.scheme?.toLowerCase()

		if(!scheme || (scheme=="file"))
		{
			File testFile = filename as File
			if (!testFile?.exists()) {
				println new Date()
				httpStatusMessage?.status = HttpStatus.NOT_FOUND
				httpStatusMessage?.message = "Not Found: ${filename}"
				log.error(httpStatusMessage?.message)
			}
			else if (!testFile?.canRead()) {
				httpStatusMessage?.status = HttpStatus.FORBIDDEN
				httpStatusMessage?.message = "Not Readable ${filename}"
				log.error(httpStatusMessage?.message)
			}
		}

		if(httpStatusMessage?.status == HttpStatus.OK)
		{
			def xml = dataInfoService.getInfo(filename)
			def background = false;
			try { background = params?.background }
			catch (Exception e) { log.error(e) }

			if (!xml) {
				httpStatusMessage?.message = "Unable to get information on file ${filename}"
				httpStatusMessage?.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
				log.error(httpStatusMessage?.message)
			}
			else if (background)
			{
				def result = stagerService.addFileToStage(filename, params.properties)

				httpStatusMessage.status = result.status
				httpStatusMessage.message = result.message

				//log.info( "submitting ${ filename } for background processing" )

				//httpStatusMessage?.message = "submitting ${ filename } for background processing".toString()

				//DataManagerQueueItem.addItem( [ file: "${ filename }", dataManagerAction: "addRaster" ],
				//		true );
			}
			else {
				def parser = parserPool?.borrowObject()
				def oms = new XmlSlurper(parser)?.parseText(xml)
				Boolean fileStaged = false
				parserPool?.returnObject(parser)

				if(params.buildOverviews||params.buildHistograms) {
					def result = stagerService.stageFileJni([filename:params.filename,
																		  buildOverviews: params.buildOverviews,
																		  buildHistograms:params.buildHistograms,
																		  overviewCompressionType: params.overviewCompressionType,
																		  buildHistogramsWithR0: params.buildHistogramsWithR0,
																		  useFastHistogramStaging: params.useFastHistogramStaging,
																		  overviewType: params.overviewType
					])
					if(result?.status >= 300) { log.error(result?.message) }
					httpStatusMessage.status = result.status
					httpStatusMessage.message = result.message
				}
				else {
					def omsInfoParser = applicationContext?.getBean("rasterInfoParser")
					def repository = ingestService?.findRepositoryForFile(filename)
					def rasterDataSets = omsInfoParser?.processDataSets(oms, repository)

					if (rasterDataSets?.size() < 1) {
						httpStatusMessage?.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
						httpStatusMessage?.message = "Not a raster file: ${filename}"
						log.error(httpStatusMessage?.message)
					}
					else {
						rasterDataSets?.each { rasterDataSet ->
							def savedRaster = true
							try {
								if (rasterDataSet.save()) {
									//stagerHandler.processSuccessful(filename, xml)
									httpStatusMessage?.status = HttpStatus.OK
									log.info(httpStatusMessage?.message)
									def ids = rasterDataSet?.rasterEntries.collect { it.id }.join(",")
									httpStatusMessage?.message = "Added raster ${ids}:${filename}"
								}
								else {
									savedRaster = false
									httpStatusMessage?.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
									httpStatusMessage?.message = "Unable to save image ${filename}, image probably already exists"
									log.error(httpStatusMessage?.message)
								}
							}
							catch (Exception e) {
								httpStatusMessage?.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
								httpStatusMessage?.message = "Unable to save image ${filename}, image probably already exists\n${e?.message}"
								log.error(httpStatusMessage?.message)
							}
						}
						//new org.ossim.omar.DataManagerQueueItem(file: filename, baseDir: parent.baseDir, dataInfo: xml).save()
					}
				}
			}
		}


      httpStatusMessage
	}

/*
def updateRaster(def httpStatusMessage, def params)
{
   if ( params.id )
   {
      def rasterEntry = RasterEntry.get(params.id)

      if ( rasterEntry )
      {
         def omsInfoParser = applicationContext.getBean("rasterInfoParser")
         def dataInfo = new DataInfo()
         def canOpen = dataInfo.open(rasterEntry.mainFile?.name)

         if ( canOpen )
         {
            def xml = dataInfo.getImageInfo(rasterEntry.entryId as int)?.trim()
            dataInfo.close()
            if ( xml )
            {
               def parser = parserPool.borrowObject()
               def oms = new XmlSlurper(parser).parseText(xml)
               parserPool.returnObject(parser)
               RasterEntry.initRasterEntry(oms?.dataSets?.RasterDataSet?.rasterEntries?.RasterEntry, rasterEntry)
               rasterEntry.save()
            }
         }
      }
      else
      {
         httpStatusMessage.message = "Query could not find record id  ${params.id} to update"
         httpStatusMessage.status = HttpStatus.NOT_FOUND
      }
   }
   else if ( params.filename )
   {
      def result = RasterDataSet.createCriteria().list {
         fileObjects {
            and {
               eq('type', "main")
               like('name', "%${params.filename}%")
            }
         }
      }
      if ( result.size() > 0 )
      {
         result.each {dataset ->
            dataset.fileObjects.each {fileObject ->
               dataset.rasterEntries.each {rasterEntry ->
                  def dataInfo = new DataInfo()
                  def canOpen = dataInfo.open(fileObject.name)

                  if ( canOpen )
                  {
                     def xml = dataInfo.getImageInfo(rasterEntry.entryId as int)?.trim()
                     dataInfo.close()
                     dataInfo = null;
                     if ( xml )
                     {
                        def parser = parserPool.borrowObject()
                        def oms = new XmlSlurper(parser).parseText(xml)
                        parserPool.returnObject(parser)
                        rasterEntry.initRasterEntry(oms?.dataSets?.RasterDataSet?.rasterEntries?.RasterEntry)
                        rasterEntry.save()
                     }
                  }
               }
            }
         }
      }
      else
      {
         httpStatusMessage.message = "Query could not find file  ${params.filename} to update"
         httpStatusMessage.status = HttpStatus.NOT_FOUND
      }
   }
}
*/

	def removeRaster( def httpStatusMessage, def params ) {
		def status = false
		String filename = params?.filename //as File

		def rasterFile = RasterFile.findByNameAndType( filename, "main" )
		if ( rasterFile ) {
			rasterFile?.rasterDataSet?.delete( flush: true )
			httpStatusMessage?.status = HttpStatus.OK
			def ids = rasterFile?.rasterDataSet?.rasterEntries?.collect { it?.id }?.join( "," )
			httpStatusMessage?.message = "removed raster ${ ids }:${ filename }"
			log.info( httpStatusMessage?.message )

			if (params.deleteFiles?.toBoolean()) {
				def files = []
				rasterFile?.rasterDataSet?.fileObjects.each() { files << it.name }
				rasterFile?.rasterDataSet?.rasterEntries.each() {
                			it.fileObjects.each() { files << it.name }
				}

				files.each() {
					def file = it
					URI uri = new URI(file)
					String scheme = uri.scheme?.toLowerCase()
					if(!scheme||(scheme=="file"))
					{
						File fileToRemove = file as File
						if (fileToRemove.canWrite()) {
							if (fileToRemove.isDirectory()) { fileToRemove.deleteDir() }
							else { fileToRemove.delete() }
							if (!fileToRemove.exists()) { log.info("Deleted ${file}") }
							else { log.info("Unable to delete ${file}") }
						}
					}
					else { log.info("Don't have permissions to delete ${file}") }
				}
			}
		}
		else {
			httpStatusMessage?.status = HttpStatus.NOT_FOUND
			httpStatusMessage?.message = "Raster file does not exist in the database: ${ filename }"
			log.error( httpStatusMessage?.message )
		}
	}


	def deleteRaster( def httpStatusMessage, def params )
	{
		removeRaster( httpStatusMessage, params )
	}


	def getFileProcessingStatus(GetRasterFilesProcessingCommand cmd)
	{
		HashMap result = [
				results:[],
				pagination: [
						count: 0,
						offset: 0,
						limit: 0
				]
		]

		try
		{
			result.pagination.count = OmarStageFile.count()
			result.pagination.offset = cmd.offset?:0
			Integer limit = cmd.limit?:result.pagination.count
			def files
//			println "FILTER === ${cmd.filter}"
//			def w = 'WHAT'
//			def query = OmarStageFile.where {
//				status=='READY'
//			}

//			if(!cmd.filter)
//			{
//				files = OmarStageFile.list([offset:result.pagination.offset, max:limit])
				files = OmarStageFile.list([offset:result.pagination.offset, max:limit])
//			}
//			else
//			{
//		       files = query.find([offset:result.pagination.offset, max:limit])

//			println files
//				 files = OmarStageFile
//			}
			//files.list([offset:result.pagination.offset, max:limit]).each{record->
			files?.each{record->
				result.results <<
						[
								filename:record.filename,
								processId: record.processId,
								status: record.status.name,
								statusMessage: record.statusMessage,
								buildOverviews: record.buildOverviews,
								buildHistograms: record.buildHistograms,
								buildHistogramsWithR0: record.buildHistogramsWithR0,
								useFastHistogramStaging: record.useFastHistogramStaging,
								overviewCompressionType: record.overviewCompressionType,
								overviewType: record.overviewType,
								dateCreated: record.dateCreated,
						]
			}

			result.pagination.limit = limit
		}
		catch(e) {
			result.status = HttpStatus.BAD_REQUEST
			result.message = e.toString()
			result.remove("results")
			result.remove("pagination")
		}

		result
	}

	def getRasterFiles(GetRasterFilesCommand cmd) {
		HashMap result = [ results:[] ]

		def files = RasterEntry.compositeId(cmd.id)

		RasterEntry entry = files?.get()
		def fileList = []
		if(entry) {
			entry.fileObjects.each{fileObject->
				fileList << fileObject.name
			}
			entry?.rasterDataSet?.fileObjects.each{ fileObject->
				fileList << fileObject.name
			}
		}
		result.results = fileList

		result
	}
}
