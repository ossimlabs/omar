package oldmar.app

import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Value

@Transactional( readOnly = true )
class WmsProxyService
{
  @Value('${oldmar.wms.endpoint}')
  String wmsEndpoint

  def handleRequest(def params)
  {
//    println params

    def contentType = params.find { it.key.toUpperCase() == 'FORMAT' }?.value ?: 'image/png'

    def newParams = params.inject( [:] ) { a, b ->
      switch ( b.key?.toUpperCase() )
      {
      case 'LAYERS':
        def layers = params['LAYERS']?.split( ',' )

        if ( layers?.every { it ==~ /\d+/ } )
        {
          a['LAYERS'] = layers?.collect { "omar:raster_entry.${it}" }.join( ',' )
        }
        else if ( layers?.every { it ==~ /[A-Fa-f0-9]{64}/ } )
        {
          a['LAYERS'] = 'omar:raster_entry'
          a['FILTER'] = "index_id in ( ${layers.collect { "'${it}'" }.join( ',' )} )"
        }
        break
      case 'CONTROLLER':
        break
      default:
        a[b.key] = b.value
      }
      a
    }.collect { "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}" }.join( '&' )

    def url = "${wmsEndpoint}?${newParams}".toURL()

//    println url

    [contentType: contentType, file: url.bytes]
  }
}
