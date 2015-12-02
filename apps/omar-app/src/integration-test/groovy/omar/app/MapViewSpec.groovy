package omar.app

import grails.test.mixin.integration.Integration
import grails.transaction.*

import spock.lang.*
import geb.spock.*

import geoscript.workspace.Workspace

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Integration
@Rollback
class MapViewSpec extends GebSpec {

    @Shared Workspace workspace

    def setupSpec()
    {
        workspace = Workspace.getWorkspace(
             dbtype: 'postgis',
             host: 'localhost',
             port: 5432,
             database: 'omardb-1.8.20-prod',
             user: 'postgres',
             passwd: 'postgres',
             'Expose primary keys': true,
             namespace: 'http://omar.ossim.org'            
        )       
    }

    def cleanupSpec()
    {
        workspace?.close()
    }

    def setup() {
    }

    def cleanup() {
    }

    void "list layers from workspace"() { 
        when: ''
        then: ''
            workspace?.names?.sort()?.each { println it }
            workspace?.names != null
    }
}
