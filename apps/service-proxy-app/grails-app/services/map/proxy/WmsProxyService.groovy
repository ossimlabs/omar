package map.proxy

import grails.transaction.Transactional

@Transactional( readOnly = true )
class WmsProxyService
{
  def grailsApplication

  def handleRequest(def params)
  {
    def inputLayer = params.find { it.key.toUpperCase() == 'LAYERS' }.value
    def format = params.find { it.key.toUpperCase() == 'FORMAT' }.value
    byte[] buffer

    //println "foo: ${grailsApplication.config.wms.proxy.list}"

    def proxyiedServer = grailsApplication.config.wms.proxy.list[inputLayer]

//    println proxyiedServer

    try
    {

      def newParams = params.inject([:]) { a, b ->
        switch ( b.key ) {
        case 'LAYERS':
          a[b.key] = inputLayer
          break
        default:
          a[b.key] = b.value
        }
        a
      }

      def baseMapURL = proxyiedServer.url
      def query = newParams.collect { "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}" }.join( '&' )
      def url = "${baseMapURL}?${query}".toURL()

      println url

      buffer = url.bytes
    }
    catch ( e )
    {
//      println e.message
    }

    [contentType: format, file: buffer]
  }
}
