package time_lapse


import grails.transaction.Transactional


@Transactional
class RestApiService {

	def grailsApplication

    
	def serviceMethod(params) {
		params.remove("action")
		params.remove("controller")

		/*
		// check for a saved link
		if (params.tlv?.isNumber()) {
			def identifier = params.tlv
			def linkExport = LinkExport.findByIdentifier(identifier)
			if (linkExport) {
				def json = new JsonSlurper().parseText(linkExport.tlvInfo)
				json.each() { params[it.key] = it.value }
			}
		}

		// if a bbox is provided, convert it an array of doubles
		if (params.bbox) { params.bbox = params.bbox.split(",").collect({ it as Double }) }
		*/

		params.availableResources = [:]
		params.availableResources.baseLayers = grailsApplication.config.baseLayers
		params.availableResources.complete = grailsApplication.config.libraries.clone()
		params.availableResources.complete.each() {
			def map = it.value.clone()

			// remove security compromising values related to each library
			map.remove("apiKey")
			map.remove("connectId")
			map.remove("username")
			map.remove("password")

			it.value = map
		}

		params.availableResources.libraries = grailsApplication.config.libraries.collect({ it.key })
		params.availableResources.sensors = grailsApplication.config.libraries.collect({ it.value.sensors }).flatten().unique({ it.name }).sort({ it.name })

		params.defaultLocation = grailsApplication.config.defaultLocation


		return params
	}
}
