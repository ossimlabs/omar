package network_specific.o2


import grails.transaction.Transactional


@Transactional
class SearchO2Service {

	def grailsApplication
	def httpDownloadService
	def library
	def mathConversionService


	def extractMetadata( json ) {
		def metadata = json.properties
		metadata.put( "footprint", json.geometry ?: null )


		return metadata
	}

	def processResults(features) {
		def images = []
		features.each() {
			def metadata = extractMetadata(it)
			def acquisitionDate = metadata.acquisition_date ? Date.parse('yyyy-MM-dd HH:mm:ss', metadata.acquisition_date.replaceAll("T", " ")) : null
			images.push([
				acquisitionDate: acquisitionDate ? acquisitionDate.format('yyyy-MM-dd HH:mm:ss') : "N/A",
				imageId: metadata.image_id ?: ( metadata.title ?: new File( metadata.filename ).name ),
				library: library.name,
				metadata: metadata,
				numberOfBands: metadata.number_of_bands ?: 1,
				type: library.layerType
			])
		}


		return images
	}

	def searchLibrary(params) {
		library = grailsApplication.config.libraries.o2

		def queryUrl = library.wfsUrl

		def filter = ""
		if (params.filter) { filter = params.filter }
		else {
			// acquisition date
			def startDate = "${params.startYear}-${params.startMonth}-${params.startDay}" +
				"T${params.startHour}:${params.startMinute}:${params.startSecond}.000+0000"
			def endDate = "${params.endYear}-${params.endMonth}-${params.endDay}T" +
				"${params.endHour}:${params.endMinute}:${params.endSecond}.999+0000"
			filter += "((acquisition_date >= ${startDate} AND acquisition_date <= ${endDate}) " +
				"OR acquisition_date IS NULL)"

			filter += " AND "

			// cloud cover
			filter += "(cloud_cover <= ${params.maxCloudCover} OR cloud_cover IS NULL)"

			filter += " AND "

			// dwithin
			def deltaDegrees = mathConversionService.convertRadiusToDeltaDegrees([radius: 1])
			filter += "DWITHIN(ground_geom,POINT(${params.location.join(" ")}),${deltaDegrees},meters)"

			filter += " AND "

			// niirs
			filter += "(niirs >= ${params.minNiirs} OR niirs IS NULL)"

			// sensors
			if (params.sensors.find { it == "all" } != "all") {
				filter += " AND "

				// only search for sensors that are available in the library
				def availableSensors = library.sensors
				def sensorFilters = []
				params.sensors.each() {
					def sensor = it
					if (sensor == availableSensors.find({ it.name == sensor }).name) { sensorFilters.push("sensor_id ILIKE '%${sensor}%'") }
				}
				sensorFilters.push("sensor_id IS NULL")
				filter += "(${sensorFilters.join(" OR ")})"
			}
		}

		queryUrl += "?filter=" + URLEncoder.encode(filter)

		queryUrl += "&maxResults=${params.maxResults}"
		queryUrl += "&outputFormat=JSON"
		queryUrl += "&request=getFeature"
		queryUrl += "&service=WFS"
		queryUrl += "&typeName=omar:raster_entry"
		queryUrl += "&version=1.1.0"
println queryUrl

		def json = httpDownloadService.serviceMethod( [url: queryUrl] )

		def images = json?.features ? processResults( json.features ) : []


		return images
	}
}
