//= require jquery-2.2.0.min.js
//= require webjars/openlayers/3.17.1/ol.js
//= require_self

var MapView = (function(){
  function init(params)
  {
    console.log(params);

    var layers = [
      new ol.layer.Tile({
        source: new ol.source.TileWMS({
          url: params.openlayers.baseMaps[4].url,
          params: {
            'LAYERS': params.openlayers.baseMaps[4].params.layers,
            'FORMAT': params.openlayers.baseMaps[4].params.format
          }
        })
      }),
      new ol.layer.Tile({
        source: new ol.source.TileWMS({
          url:  (params.contextPath || '') + '/evwhs/wms',
          params: {
          }
        })
      })
    ];

    var map = new ol.Map({
      controls: ol.control.defaults().extend([
        new ol.control.ScaleLine({
          units: 'degrees'
        })
      ]),
      layers: layers,
      target: 'map',
      view: new ol.View({
        projection: 'EPSG:4326',
        center: [0, 0],
        zoom: 2
      })
    });
  }
  return {
    init: init
  };
})();
