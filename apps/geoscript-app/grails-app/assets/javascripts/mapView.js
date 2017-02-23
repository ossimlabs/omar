//= require jquery-2.2.0.min.js
//= require webjars/openlayers/3.17.1/ol.js
//= require_self

var MapView = (function(){
  function init(params)
  {
    var layers = [
            new ol.layer.Tile({
              source: new ol.source.TileWMS({
                url: 'http://web.dev.o2.ossimc2s.com/service-proxy/wmsProxy',
                params: {
                  'LAYERS': 'o2-basemap-basic',
                  'FORMAT': 'image/jpeg'
                }
              })
            }),
            new ol.layer.Tile({
              source: new ol.source.TileWMS({
                url: '/evwhs/wms',
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
              // projection: 'EPSG:4326',
              center: [0, 0],
              zoom: 2
            })
          });  }
  return {
    init: init
  };
})();
