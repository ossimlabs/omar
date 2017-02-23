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
          url: params.refMap.url,
          params: {
            'LAYERS': params.refMap.layer,
            'FORMAT': 'image/jpeg'
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
