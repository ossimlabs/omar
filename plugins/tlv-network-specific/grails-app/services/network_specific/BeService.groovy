package network_specific


import grails.transaction.Transactional


@Transactional
class BeService {

	def grailsApplication
	def httpDownloadService


	def serviceMethod( params ) {

		def url = "${ grailsApplication.config.beLookup.url }?" +
			"filter=" + URLEncoder.encode( "${ grailsApplication.config.beLookup.columnName } = '${ params.beNumber }'" ) + "&" +
			"maxFeatures=1&" +
			"outputFormat=JSON&" +
			"request=GetFeature&" +
			"service=WFS&" +
			"typeName=${ grailsApplication.config.beLookup.typeName }&" +
			"version=1.1.0"

		def json = httpDownloadService.serviceMethod( [url: url] )

		def location = []
		if (json.features.size() > 0) {
			location = json.features[0].geometry?.coordinates
		}


		return location
	}
}
