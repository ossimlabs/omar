package network_specific


import grails.transaction.Transactional


@Transactional
class SearchLibraryService {

	def grailsApplication
	def httpDownloadService
	def mathConversionService
	def searchO2Service


	def serviceMethod(params) {
		def results = [
			layers: searchO2Service.searchLibrary( params ),
			location: params.location.collect({ it as Double })
		]

		if (results.layers.size() > params.maxResults) {
			def howManyToDrop = results.layers.size() - params.maxResults
			results.layers = results.layers.reverse().drop(howManyToDrop).reverse()
		}

		results.layers.sort({ it.acquisitionDate })


		return results
	}
}
