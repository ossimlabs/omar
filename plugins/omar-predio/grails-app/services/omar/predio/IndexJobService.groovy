package omar.predio

import grails.transaction.Transactional

@Transactional
class IndexJobService {

    synchronized def nextJob()
    {
        def firstObject = PredioIndexJob.first()
        def result = firstObject?.properties
        result = result?:[:]

        firstObject?.delete()

        result
    }
}
