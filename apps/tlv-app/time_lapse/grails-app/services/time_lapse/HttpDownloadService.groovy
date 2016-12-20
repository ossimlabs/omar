package time_lapse


import grails.transaction.Transactional
import static groovyx.net.http.ContentType.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import java.security.KeyStore
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory


@Transactional
class HttpDownloadService {


	def serviceMethod(params) {
		def http = new HTTPBuilder(params.url)

		def keyStoreFile = getClass().getResource("/keyStore.jks")
		def trustStoreFile = getClass().getResource("/trustStore.jks")

		if (keyStoreFile && trustStoreFile) {
			def keyStore = KeyStore.getInstance(KeyStore.defaultType)
			keyStoreFile.withInputStream { keyStore.load(it, "tlvCheese".toCharArray()) }

			def trustStore = KeyStore.getInstance(KeyStore.defaultType)
			trustStoreFile.withInputStream { trustStore.load(it, "tlvCheese".toCharArray()) }

			def ssl = new SSLSocketFactory(keyStore, "tlvCheese", trustStore)
			http.client.connectionManager.schemeRegistry.register(new Scheme("https", ssl, 443))
		}

		try {
			http.request(params.method ?: GET) { req ->
				if (params.username && params.password) {
					params.auth = "${params.username}:${params.password}".getBytes().encodeBase64()
				}
				if (params.auth) { headers."Authorization" = "${params.authType ?: "Basic"} ${params.auth}" }

				if (params.body) { send URLENC, params.body }

				response.failure = { resp, reader ->
println "Failure: ${reader}"


					return null
				}
				response.success = { resp, reader ->
					def contentType = resp.allHeaders.find({ it.name =~ /(?i)Content-Type/})
					if (contentType) { contentType = contentType.value }


					if (contentType.contains("image/jpeg") || contentType.contains("image/png")) { return reader.bytes }
					else { return reader }
				}
			}
		}
		catch (Exception e) {
			println e
			return null
		}
	}
}
