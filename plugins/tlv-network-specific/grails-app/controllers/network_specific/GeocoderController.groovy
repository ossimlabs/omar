package network_specific


import groovy.json.JsonOutput


class GeocoderController {

	def geocoderService


	def index() { 
		def result = geocoderService.serviceMethod( params )


		render JsonOutput.toJson(result)
	}
}
