package oldmar.app

import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Value

@Transactional( readOnly = true )
class WfsProxyService
{
  @Value( '${oldmar.wfs.endpoint}' )
  String wfsEndpoint

  def handleRequest(def params)
  {
    def newParams = params.inject( [:] ) { a, b ->
      switch ( b.key?.toUpperCase() )
      {
      case 'CONTROLLER':
        break
      default:
        a[b.key] = b.value
      }
      a
    }.collect { "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}" }.join( '&' )

    def url = "${wfsEndpoint}?${newParams}".toURL()
    def urlConnection = url.openConnection();
    //def contentType = urlConnection.getHeaderField( "Content-Type" );
    def contentType = 'text/xml'

    [contentType: contentType, file: url.bytes]
  }
}
