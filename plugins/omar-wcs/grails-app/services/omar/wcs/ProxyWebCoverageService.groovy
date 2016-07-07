package omar.wcs

import grails.transaction.Transactional

@Transactional( readOnly = true )
class ProxyWebCoverageService
{
  def grailsLinkGenerator

  def getCapabilities(def params)
  {
    println "getCapabilities: ${params}"

    def file = '/tmp/gs-wcs-caps.xml' as File

    [contentType: '', buffer: file.text]
  }

  def describeCoverage(def params)
  {
    println "describeCoverage: ${params}"

    def file = '/tmp/gs-wcs-descov.xml' as File

    [contentType: '', buffer: file.text]
  }

  def getCoverage(def params)
  {
    println "getCoverage: ${params}"

    def geoserverWFS = "http://localhost:8080/geoserver/wfs"

    def getCovParams = params.collect {
      "${it.key}=${URLEncoder.encode( ( it.value ?: '' ) as String, 'UTF-8' )}"
    }.join( '&' )

    def url = "${geoserverWFS}?${getCovParams}".toURL()

    [contentType: 'image/tiff', buffer: url.bytes]
  }

}
