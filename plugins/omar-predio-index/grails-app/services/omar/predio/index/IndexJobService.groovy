package omar.predio.index

import grails.transaction.Transactional
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.URLENC

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
    def postItem(def item, def locations, def categories, def eventTime = null, def expireDate=null)
    {

//        System.sleep(1000)
//        println "********************${predioService}*********************"
//        def config = OmarPredioIndexUtils.predioIndexConfig
//        URL url = new URL(config.predioUrl+"/setItem")
//        String urlPart = "${url.protocol}://${url.authority}"
//        String pathPart = url.path
//
////        def http = new HTTPBuilder(urlPart)
//        def locationsString = locations.join(",")
//        def categoriesString = categories.join(",")
//
//        println "***************************"
//
//
//        def http = new HTTPBuilder( urlPart )
//        def postBody = [item:item] // will be url-encoded
//        if(locationsString) postBody.locations = locationsString
//        if(categoriesString) postBody.categories = categoriesString
//        if(eventTime) postBody.eventTime   = eventTime.toString()
//        if(expireDate) postBody.expireDate = expireDate.toString()
//
//        http.post( path: pathPart, body: postBody,
//                requestContentType: URLENC ) { resp ->
//
//            println "POST Success: ${resp.statusLine}"
//            //assert resp.statusLine.statusCode == 201
//        }

//        http.request(Method.POST, ContentType.TEXT) {
//            uri.path = pathPart
//            uri.query = [item:item]
//            if(locationsString) uri.query.locations = locationsString
//            if(categoriesString) uri.query.categories = categoriesString
//
//            println uri
//            //println "PATH: ${uri.path}"
//            //println "QUERY: ${uri.query}"
//            if(eventTime) uri.query.eventTime   = eventTime.toString()
//            if(expireDate) uri.query.expireDate = expireDate.toString()
//            //headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
//
//            // response handler for a success response code
//            response.success = { resp, reader ->
//                println "response status: ${resp.statusLine}"
//                println 'Headers: -----------'
//                resp.headers.each { h ->
//                    println " ${h.name} : ${h.value}"
//                }
//
//                def ret = reader.getText()
//
//                println 'Response data: -----'
//                println ret
//                println '--------------------'
//            }
//        }
    }
}
