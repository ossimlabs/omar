package time_lapse


import groovy.json.JsonOutput
import groovy.json.JsonSlurper


class SearchController {

	def logsService
	def searchLibraryService


	def searchLibrary() {
		def searchParams = new JsonSlurper().parseText(params.searchParams)
		logsService.recordImagerySearch(searchParams, request)

		def results = searchLibraryService.serviceMethod(searchParams)


		render JsonOutput.toJson(results)
	}
}
