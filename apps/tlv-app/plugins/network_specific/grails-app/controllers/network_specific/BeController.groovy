package network_specific


import groovy.json.JsonOutput


class BeController {

	def beService


	def index() { 
		def result = beService.serviceMethod( params )


		render JsonOutput.toJson(result)
	}
}
