package omar.stager


import omar.core.Repository

//import omar.core.HttpStatusMessage
//import static groovyx.gpars.GParsPool.withPool

import grails.transaction.Transactional

@Transactional
class StagerService
{
  static transactional = true
  def dataManagerService
  def grailsApplication
  def sessionFactory

  def runStager(Repository repository)
  {

    repository.scanStartDate = new Date()
    repository.scanEndDate = null
    repository.save()

    StagerJob.triggerNow( [baseDir: repository.baseDir] )
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
}
