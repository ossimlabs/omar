package omar.predio.index

import grails.transaction.Transactional

@Transactional
class IndexJobService {

    def predioService

    synchronized def nextJob()
    {
        def firstObject = PredioIndexJob.first()
        def result = firstObject?.properties
        result = result?:[:]

        firstObject?.delete()

        result
    }
}
