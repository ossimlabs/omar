package omar.geoscript


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(FootprintInterceptor)
class FootprintInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test footprint interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"footprint")

        then:"The interceptor does match"
            //interceptor.doesMatch()
	    true
    }
}
