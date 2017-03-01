package time_lapse


import grails.transaction.Transactional
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import java.security.KeyStore
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory


@Transactional
class HttpDownloadService {


	def serviceMethod(params) {
		def http = new HTTPBuilder( params.url )

		def keyStoreFile = getClass().getResource( "/keyStore.jks" )
		def trustStoreFile = getClass().getResource( "/trustStore.jks" )

		if ( keyStoreFile && trustStoreFile ) {
			def keyStore = KeyStore.getInstance( KeyStore.defaultType )
			keyStoreFile.withInputStream { keyStore.load( it, "tlvCheese".toCharArray() ) }

			def trustStore = KeyStore.getInstance( KeyStore.defaultType )
			trustStoreFile.withInputStream { trustStore.load( it, "tlvCheese".toCharArray() ) }

			def ssl = new SSLSocketFactory( keyStore, "tlvCheese", trustStore )
			http.client.connectionManager.schemeRegistry.register( new Scheme( "https", ssl, 443 ) )
		}

		try {
			http.request( GET ) { req ->
				response.failure = { resp, reader ->
					println "Failure: ${reader}"


					return null
				}
				response.success = { resp, reader ->


					return reader
				}
			}
		}
		catch (Exception e) {
			println e
			return null
		}
	}
}
