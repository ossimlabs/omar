package time_lapse


import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.*


/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class MathConversionServiceSpec extends Specification {

	def setup() {}

	def cleanup() {}

	void "convertRadiusToDeltaDegrees"() {
		def service = new MathConversionService()
		def radius = 525 // meters
		def degrees = service.convertRadiusToDeltaDegrees([ radius: radius ])

		expect:
			degrees.toString().isNumber()
			degrees > 0.0047246220
			degrees < 0.0047246221
			
	}
}
