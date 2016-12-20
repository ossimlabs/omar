package network_specific.o2


import grails.transaction.Transactional


@Transactional
class SearchO2Service {

	def grailsApplication
	def httpDownloadService
	def library
	def mathConversionService


	def extractMetadata(xml) {
		def metadata = [
			accessDate: xml.access_date?.text() ?: null,
			acquisitionDate: xml.acquisition_date?.text() ?: null,
			azimuthAngle: xml.azimuth_angle?.text().isNumber() ? xml.azimuth_angle.text() as Double : null,
			beNumber: xml.be_number?.text() ?: null,
			bitDepth: xml.bit_depth?.text() ?: null,
			className: xml.class_name?.text() ?: null,
			cloudCover: xml.cloud_cover?.text().isNumber() ? xml.cloud_cover.text() as Double : null,
			countryCode: xml.country_code?.text() ?: null,
			dataType: xml.data_type?.text() ?: null,
			description: xml.description?.text() ?: null,
			entryId: xml.entry_id?.text().isNumber() ? xml.entry_id.text() as Integer : null,
			excludePolicy: xml.exclude_policy?.text() ?: null,
			fileType: xml.file_type?.text() ?: null,
			filename: xml.filename?.text() ?: null,
			grazingAngle: xml.grazing_angle?.text().isNumber() ? xml.grazing_angle.text() as Double : null,
			gsdUnit: xml.gsd_unit?.text() ?: null,
			gsdX: xml.gsdx?.text().isNumber() ? xml.gsdx.text() as Double : null,
			gsdY: xml.gsdy?.text().isNumber() ? xml.gsdy.text() as Double : null,
			height: xml.height?.text().isNumber() ? xml.height.text() as Integer : null,
			id: xml.id?.text() ?: null,
			imageCategory: xml.image_category?.text() ?: null,
			imageId: xml.image_id?.text() ?: null,
			imageRepresentation: xml.image_representation?.text() ?: null,
			indexId: xml.index_id?.text() ?: null,
			ingestDate: xml.ingest_date?.text() ?: null,
			isorce: xml.isorce?.text() ?: null,
			leepForever: xml.keep_forever?.text() ?: null,
			missionId: xml.mission_id?.text() ?: null,
			niirs: xml.niirs?.text().isNumber() ? xml.niirs.text() as Double : null,
			numberOfBands: xml.number_of_bands?.text().isNumber() ? xml.number_of_bands.text() as Integer : null,
			numberOfResLevels: xml.number_of_res_levels?.text().isNumber() ? xml.number_of_res_levels.text() as Integer : null,
			organization: xml.organization?.text() ?: null,
			productId: xml.productId?.text() ?: null,
			receiveDate: xml.receive_date?.text() ?: null,
			releaseId: xml.release_id?.text().isNumber() ? xml.release_id.text() as Double : null,
			securityClassification: xml.security_classification?.text() ?: null,
			securityCode: xml.security_code?.text() ?: null,
			sensorId: xml.sensor_id?.text() ?: null,
			sunAzimuth: xml.sun_azimuth?.text() ? xml.sun_azimuth.text() as Double : null,
			sunElevation: xml.sun_elevation?.text().isNumber() ? xml.sun_elevation.text() as Double : null,
			targetId: xml.target_id?.text() ?: null,
			title: xml.title?.text() ?: null,
			wacCode: xml.wac_code?.text() ?: null,
			width: xml.width?.text().isNumber() ? xml.width.text() as Integer : null,
			version: xml.version?.text() ?: null
		]

		def footprint = xml.ground_geom?.MultiPolygon?.polygonMember?.Polygon?.outerBoundaryIs?.LinearRing?.coordinates?.text()
		metadata.put("footprint", footprint ? footprint?.split(" ").collect({ it.split(",").collect({ it as Double}) }) : null)


		return metadata
	}

	def processResults(features) {
		def images = []
		features.each() {
			def metadata = extractMetadata(it)

			def image = [:]
			image.acquisitionDate = metadata.acquisitionDate
			image.indexId = metadata.indexId
			image.imageId = metadata.imageId ?: (metadata.title ?: new File(metadata.filename).name)
			image.library = library.name
			image.metadata = metadata
			image.type = library.layerType
			images.push(image)
		}


		return images
	}

	def searchLibrary(params) {
		library = grailsApplication.config.libraries.o2

		def queryUrl = library.queryUrl

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
			filter += "(cloud_cover < ${params.maxCloudCover} OR cloud_cover IS NULL)"

			filter += " AND "

			// dwithin
			def deltaDegrees = mathConversionService.convertRadiusToDeltaDegrees([radius: 1])
			filter += "DWITHIN(ground_geom,POINT(${params.location.join(" ")}),${deltaDegrees},meters)"

			filter += " AND "

			// niirs
			filter += "(niirs < ${params.minNiirs} OR niirs IS NULL)"

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
		queryUrl += "&request=getFeature"
		queryUrl += "&service=WFS"
		queryUrl += "&typeName=omar:raster_entry"
		queryUrl += "&version=1.1.0"
println queryUrl

		def xml = httpDownloadService.serviceMethod([url: queryUrl])

		def images = xml?.featureMembers ? processResults(xml.featureMembers.raster_entry) : []


		return images
	}
}
