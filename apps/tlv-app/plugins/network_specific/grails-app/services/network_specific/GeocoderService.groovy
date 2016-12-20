package network_specific


import grails.transaction.Transactional


@Transactional
class GeocoderService {

	def grailsApplication
	def httpDownloadService


	def serviceMethod( params ) {
		def url = "${grailsApplication.config.geocoderUrl}?" +
			"autocomplete=true&" +
			"autocompleteBias=BALANCED&" + 
			"maxInterpretations=1&" +
			"query=${ URLEncoder.encode( params.location ) }&" +
			"responseIncludes=WKT_GEOMETRY_SIMPLIFIED"
		def json = httpDownloadService.serviceMethod( [url: url] )

		def location = []
		if (json.interpretations.size() > 0) {
			def center = json.interpretations[0].feature?.geometry?.center
			location << center.lng
			location << center.lat
		}

		
		return location
	}
}
