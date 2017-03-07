package three_disa


import groovy.json.JsonOutput
import groovy.json.JsonSlurper


class ThreeDisaController {

	def threeDisaService


	def listJobs() {
		def jobs = threeDisaService.getJobListing( params, request )
		def json = new JsonOutput().toJson( jobs )


		render json
	}

	def submit3DisaJob() {
		def json = new JsonSlurper().parseText( params.jobParams )
		def response = threeDisaService.submitJob( json, request )


		render new JsonOutput().toJson( response )
	}
}
