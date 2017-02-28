package time_lapse


import grails.transaction.Transactional
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET


@Transactional
class HttpDownloadService {


	def serviceMethod(params) {
		try {
			def command = "curl -Lk ${ params.url }"
println command
			def process = command.execute()
			process.waitFor()
			def text = process.getText()
			def json = new JsonSlurper().parseText( text )


			return json
			//http.request( GET ) { req ->
			//	response.failure = { resp, reader ->
//println "Failure: ${reader}"


//					return null
//				}
//				response.success = { resp, reader ->


//					return reader
//				}
//			}
		}
		catch ( Exception e ) {
			println e


			return null
		}
	}
}
