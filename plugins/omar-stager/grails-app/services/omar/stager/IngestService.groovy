package omar.stager

import omar.core.Repository
import omar.core.HttpStatus
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class IngestService implements ApplicationContextAware
{
	static transactional = true

	def applicationContext


	def ingest( def oms, def baseDir = '/' )
	{
		def status  = HttpStatus.OK
		def message = ""

		if ( oms )
		{
			def omsInfoParsers = applicationContext.getBeansOfType( OmsInfoParser.class )
			def repository = Repository.findByBaseDir( ( baseDir as File ).absolutePath )

			for ( def parser in omsInfoParsers?.values() )
			{
				def dataSets = parser.processDataSets( oms, repository )

				for ( def dataSet in dataSets )
				{
					if ( dataSet.save() )
					{
						status = HttpStatus.OK
						message = "Added dataset"
					}
					else
					{
						status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
						message = ""
						dataSet.errors.allErrors.each{

							if(message)	message = "${message}\n${it}".toString()
							else message = it.toString()
						}
					}
				}
			}
		}

		return [ status, message ]
	}

	synchronized def findRepositoryForFile( def file )
	{
		def repository

		if ( File.separatorChar == '\\' )
		{
			// I am having troubles with windows.  We will address this when we refactor the
			// repo implementation
			//
			repository = Repository.findByBaseDir( "/" );

			if ( !repository )
			{
				repository = new Repository( baseDir: "/" )
				repository.save( flush: true )
				log.debug( "Creating default repository /" )
			}

			return repository
		}
	}

	void setApplicationContext( ApplicationContext applicationContext )
	{
		this.applicationContext = applicationContext
	}
}
