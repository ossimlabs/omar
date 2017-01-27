package oldmar.app

import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Value

@Transactional( readOnly = true )
class FootprintsProxyService
{
  @Value( '${oldmar.footprints.endpoint}' )
  String footprintsEndpoint

  @Value( '${oldmar.footprints.overrideFormat}' )
  String overrideFormat

  @Value( '${oldmar.footprints.defaultStyle}' )
  String defaultStyle

  def handleRequest(def params)
  {
//    println params
    def format = params.find { it.key.toUpperCase() == 'FORMAT' }?.value
    def contentType = (overrideFormat || !format) ? overrideFormat : format

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
      case 'FORMAT':
        if ( overrideFormat?.trim()) {
          println "overrideFormat = ${overrideFormat}"
          a['FORMAT'] = overrideFormat
        } else {
          a['FORMAT'] = b.value
        }
        break
      case 'CONTROLLER':
        break
      default:
        a[b.key] = b.value
      }
      a
    }

    // println newParams
    newParams = newParams.collect { "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}" }.join( '&' )

    def url = "${footprintsEndpoint}?${newParams}".toURL()
    def bytes

    try {
      bytes = url.bytes
    } catch ( e ) {
        e.printStackTrace()
    }

    [contentType: contentType, file: bytes]
  }
}
