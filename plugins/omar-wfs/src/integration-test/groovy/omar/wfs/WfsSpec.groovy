package omar.wfs

import geoscript.workspace.WFS
import grails.test.mixin.integration.Integration
import grails.transaction.*

import spock.lang.*
import geb.spock.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Integration
@Rollback
class WfsSpec extends GebSpec
{
  @Shared
  WFS wfs

  @Shared
  def grailsApplication

  @Shared
  def grailsLinkGenerator

  def setupSpec()
  {
    def wfsServiceAddress = 'http://localhost/geoserver/wfs'
//    def wfsServiceAddress = 'http://localhost:9999/omar/wfs'

//    def wfsServiceAddress = grailsLinkGenerator.link( absolute: true, uri: '/wfs' )

//    wfs = new WFS( [protocol: 'get'], "${wfsServiceAddress}?service=WFS&version=1.1.0&request=GetCapabilities" )
  }

  def cleanupSpec()
  {
    // wfs?.close()
  }

  def setup()
  {
  }

  def cleanup()
  {
  }

  void "connect to WFS server"()
  {
  when: ""

  then: ""
    //def names = wfs?.names?.sort()?.each { println it }
    //def names = wfs?.layers*.name?.sort()?.each { println it }

/*
    def serverBase = grailsLinkGenerator.serverBaseURL?.toURL()
    def serverPort = System.getenv( 'SERVER_PORT' ) ?: '8080'
    def serverURL = "${serverBase}:${serverPort}${grailsLinkGenerator.contextPath ?: ''}"

    def wfsServiceAddress = grailsLinkGenerator.link( base: serverURL, uri: '/wfs', params: [
        service: 'WFS', version: '1.1.0', request: 'GetCapabilities'] )

    println "linkGen: ${wfsServiceAddress}"
*/
    //println wfs['topp:states'].count()
    //names != null
    true
  }
}
