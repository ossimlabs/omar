package time_lapse


class LogsController {


	def imagerySearch() {
		if (params.max?.isNumber()) { params.max = params.max.toInteger() > 1000 ? 1000 : params.max }
		else { params.max = 1000 }	
		respond ImagerySearch.list(params), model:[imagerySearchCount: ImagerySearch.count()]
	}

	def wmsRequest() {
		if (params.max?.isNumber()) { params.max = params.max.toInteger() > 100 ? 100 : params.max }
		else { params.max = 10 }
		respond WmsRequest.list(params), model:[wmsRequestCount: WmsRequest.count()]
	}

	def xyzRequest() {
		if (params.max?.isNumber()) { params.max = params.max.toInteger() > 100 ? 100 : params.max }
		else { params.max = 10 }
                respond XyzRequest.list(params), model:[xyzRequestCount: XyzRequest.count()]
	}
}
