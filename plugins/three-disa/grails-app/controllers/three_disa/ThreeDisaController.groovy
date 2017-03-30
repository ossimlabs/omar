package three_disa


import grails.converters.JSON
import groovy.json.JsonSlurper


class ThreeDisaController {

	def threeDisaService


	def listJobs() {
		def jobs = threeDisaService.getJobListing( params, request )
		JSON.use("deep")


		render jobs as JSON
	}

	def submit3DisaJob() {
		def json = new JsonSlurper().parseText( params.jobParams )
		def response = threeDisaService.submitJob( json, request )


		render response as JSON
	}
}
