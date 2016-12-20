package time_lapse


import grails.transaction.Transactional


@Transactional
class LogsService {
		

	def getIpAddress(request) { 
		["client-ip", "x-cluster-client-ip", "x-forwarded-for"].each() {
			def ip = request.getHeader(it)
			if (ip) { return ip }
		}


		return "N/A"
	}

	def recordImagerySearch(params, request) {
		def endDateString = "${params.endYear}-${params.endMonth}-${params.endDay} ${params.endHour}:${params.endMinute}:${params.endSecond}"
		def endDate = Date.parse("yyyy-MM-dd HH:mm:ss", endDateString)

		def startDateString = "${params.startYear}-${params.startMonth}-${params.startDay} ${params.startHour}:${params.startMinute}:${params.startSecond}"
		def startDate = Date.parse("yyyy-MM-dd HH:mm:ss", startDateString)

		new ImagerySearch(
			date: new Date(),
			endDate: endDate,
			ipAddress: getIpAddress(request),
			library: params.libraries.join(","),
			location: params.location,
			maxCloudCover: params.maxCloudCover,
			maxResults: params.maxResults,
			minNiirs: params.minNiirs,
			sensors: params.sensors.join(","),
			startDate: startDate
		).save()
	}

	def recordWmsRequest(params, request) {
		new WmsRequest(
			bbox: params.BBOX,
			date: new Date(),
			imageId: params.IMAGE_ID,
			ipAddress: getIpAddress(request),
                        library: params.LIBRARY	
		).save()
	}

	def recordXyzRequest(params, request) {
		new XyzRequest(
			date: new Date(),
			imageId: params.IMAGE_ID,
			ipAddress: getIpAddress(request),
			library: params.LIBRARY,
			x: params.X,
			y: params.Y,
			z: params.Z
		).save()
	}
}
