package omar.stager


import omar.core.Repository

//import omar.core.HttpStatusMessage
//import static groovyx.gpars.GParsPool.withPool

import grails.transaction.Transactional

@Transactional
class StagerService
{
	static transactional = true
	//def dataManagerService
	def grailsApplication
	def sessionFactory


	def parserPool
	def ingestService
  def dataInfoService

	enum Action {
		BUILD_OVRS,
		INDEX_ONLY
	}

	def runStager( Repository repository )
	{

		repository.scanStartDate = new Date()
		repository.scanEndDate = null
		repository.save()

		StagerJob.triggerNow( [ baseDir: repository.baseDir ] )
	}

	def cleanUpGorm()
	{
		def session = sessionFactory.currentSession
		session.flush()
		session.clear()
	}

/*
  def popAndAddStagerQueueItem()
  {
    def result = 0
    def nthreads = grailsApplication.config.stager.queue.threads ?: 4
    try
    {
      StagerQueueItem.withTransaction {
        def records = [];

        try
        {
          records = StagerQueueItem.list( cache: false,
              sort: "dateCreated",
              max: 100,
              order: "desc" )

        }
        catch ( def e )
        {
          cleanUpGorm()
        }
        try
        {
          records.each { record ->
            record.status = "indexing"
            record.save()
          }
        }
        catch ( def e )
        {
          cleanUpGorm()
        }
        withPool() {
          records.collectParallel { item ->
            def msg = new HttpStatusMessage();
            dataManagerService.add( msg, [datainfo: item.dataInfo] )
          }
        }
        result += records.size();
        records.each { record ->
          record.delete()
        }
      }
    }
    catch ( def e )
    {
      println e
    }

    cleanUpGorm()
    result
  }
*/

	def stageFile( String filename, String baseDir='/' )
	{
		def results
		try
		{
			//filesLog.append("${file.absolutePath}\n")
			def start = System.currentTimeMillis()
			def xml = null
			def action = Action.BUILD_OVRS

			switch ( action )
			{
			case Action.BUILD_OVRS:
				xml = StagerUtil.getInfo( filename as File)
				break
			case Action.INDEX_ONLY:
				xml = dataInfoService.getInfo( filename.absolutePath as File)
				break
			}

			if ( xml )
			{
				def parser = parserPool.borrowObject()
				def oms = new XmlSlurper( parser ).parseText( xml )

				parserPool.returnObject( parser )

				def ( status, message ) = ingestService.ingest( oms, baseDir )

				results = message

//				switch ( status )
//				{
//				case 200:
//					//          filesLog.println file.absolutePath
//					break
//				case 500:
//					//          rejectsLog.println "${file.absolutePath} ${message}"
//					break
//				}
			}
			else
			{
//				rejectsLog.println file.absolutePath
			}

			//      if ( index?.incrementAndGet() % batchSize == 0 )
			//      {
			//        cleanUpGorm()
			//      }
			//
			def end = System.currentTimeMillis()
		}
		catch ( Exception e )
		{
			//println "ERROR: ${ filename } ${ e.message }"
			e.printStackTrace(  )
		}

		return results
	}
}
