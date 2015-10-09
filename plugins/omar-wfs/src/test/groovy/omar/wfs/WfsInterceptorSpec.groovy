package omar.wfs


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(WfsInterceptor)
class WfsInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test wfs interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"wfs")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
