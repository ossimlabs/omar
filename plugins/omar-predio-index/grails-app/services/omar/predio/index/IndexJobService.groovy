package omar.predio.index

import grails.transaction.Transactional
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

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
    def postItem(def item, def locations, def categories, def eventTime = null, def expireDate=null)
    {
        def config = OmarPredioIndexUtils.predioIndexConfig
        URL url = config.predioUrl
        String urlPart = "${url.protocol}://${url.authority}"
        String pathPart = url.path


        def http = new HTTPBuilder(urlPart)

        http.request(Method.POST, ContentType.TEXT) {
            uri.path = pathPart
            uri.query = [item:item, locations:locations?.join(","), categories:categories?.join(",")]
            if(eventTime) uri.query.eventTime   = eventTime.toString()
            if(expireDate) uri.query.expireDate = expireDate.toString()
            //headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

            // response handler for a success response code
            response.success = { resp, reader ->
                println "response status: ${resp.statusLine}"
                println 'Headers: -----------'
                resp.headers.each { h ->
                    println " ${h.name} : ${h.value}"
                }

                ret = reader.getText()

                println 'Response data: -----'
                println ret
                println '--------------------'
            }
        }
    }
}
