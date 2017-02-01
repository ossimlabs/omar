package oldmar.app

import org.springframework.beans.factory.annotation.Value

class MapViewController
{
  @Value( '${oldmar.mapView.baseLayer.url}' )
  String baseLayerUrl

  @Value( '${oldmar.mapView.baseLayer.layers}' )
  String baseLayerNames

  @Value( '${oldmar.mapView.baseLayer.format}' )
  String baseLayerFormat

  @Value( '${oldmar.mapView.defaults.wms.layers}' )
  String defaultWmsLayers

  @Value( '${oldmar.mapView.defaults.footprints.time}' )
  String defaultFootprintsTime

  @Value( '${oldmar.mapView.defaults.footprints.useDefault}' )
  Boolean useDefaultFootprintsTime

  @Value( '${oldmar.mapView.defaults.footprints.format}' )
  String footprintsFormat
  
  def index()
  {
    if ( params.containsKey( 'useDefaultFootprintsTime' ) )
    {
      useDefaultFootprintsTime = params.boolean( 'useDefaultFootprintsTime' )
    }

    def mapViewParams = [
        baseLayer: [url: baseLayerUrl, layers: baseLayerNames, format: baseLayerFormat],
        contextPath: grailsApplication.config.server.contextPath ?: "",
        wmsLayers: params.wmsLayers ?: defaultWmsLayers,
        footprintsTime: params.footprintsTime ?: ( useDefaultFootprintsTime ) ? defaultFootprintsTime : '',
        footprintsFormat: footprintsFormat
    ]

    [mapViewParams: mapViewParams]
  }
}
