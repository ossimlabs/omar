package omar.wms

import groovy.json.JsonOutput

class OldMARCompatibility
{
  static final STYLE_PARAM_NAMES = [
    'STRETCH_MODE', 'STRETCH_MODE_REGION', 'BANDS', 'INTERPOLATION',
    'BRIGHTNESS', 'CONTRAST'
  ]

  static def translate( def params )
  {
    def styleParams = params?.findAll { it?.key?.toUpperCase() in STYLE_PARAM_NAMES }?.inject([:]) { a, b ->
        switch ( b.key.toUpperCase() )
        {
        case 'STRETCH_MODE':
            a.hist_op = b.value
            break
        case 'STRETCH_MODE_REGION':
            break
        case 'BANDS':
            a.bands = b.value
            break
        case 'BRIGHTNESS':
            a.brightness = b.value
            break
        case 'CONTRAST':
            a.contrast = b.value
            break
        case 'INTERPOLATION':
            a.resampler_filter = b.value
            break
        case 'SHARPEN_MODE':
            a.sharpen_mode = b.value
            break
        }
        a
    }

    if ( styleParams )
    {
      params.styles = JsonOutput.toJson(styleParams)
    }

    def layerList = params?.layers?.split(',')

    if ( layerList?.every { it ==~ /\d+/ } )
    {
      params.filter = "in(${params?.layers})"
      params.layers = 'omar:raster_entry'
    }
    else if ( layerList?.every { it ==~ /[A-Fa-f0-9]{64}/ })
    {
      params.filter = "index_id in(${params?.layers})"
      params.layers = 'omar:raster_entry'
    }
  }
}
