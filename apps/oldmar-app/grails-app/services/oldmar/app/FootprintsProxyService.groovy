package oldmar.app

import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Value

@Transactional( readOnly = true )
class FootprintsProxyService
{
  @Value( '${oldmar.footprints.endpoint}' )
  String footprintsEndpoint

  @Value( '${oldmar.footprints.defaultStyle}' )
  String defaultStyle

  def handleRequest(def params)
  {
//    println params

    def contentType = params.find { it.key.toUpperCase() == 'FORMAT' }?.value ?: 'image/png'

    def newParams = params.inject( [:] ) { a, b ->
      switch ( b.key?.toUpperCase() )
      {
      case 'LAYERS':
        if ( params['LAYERS'] == 'Imagery' )
        {
          a['LAYERS'] = 'omar:raster_entry'
        }
        else if ( params['LAYERS'] == 'omar:video_data_set' )
        {
          a['LAYERS'] = b.value
        }
        break
      case 'STYLES':
        a['STYLES'] = defaultStyle
        break
      case 'TIME':
        if ( b.value?.trim() )
        {
          a['TIME'] = b.value
        }
        break
      case 'CONTROLLER':
        break
      default:
        a[b.key] = b.value
      }
      a
    }.collect { "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}" }.join( '&' )

    def url = "${footprintsEndpoint}?${newParams}".toURL()

    [contentType: contentType, file: url.bytes]
  }
}
