package time_lapse


import grails.transaction.Transactional


class MathConversionService {


	def convertRadiusToDeltaDegrees(params) {
		def radius = params.radius as Integer

		/* #m * 1Nm / 1852m * 1min / 1Nm * 1deg / 60min */
		def deltaDegrees = radius / 1852 / 60


		return deltaDegrees
	}
}
