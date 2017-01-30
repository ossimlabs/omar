//= require jquery-2.2.0.min.js
//= require webjars/openlayers/3.20.1/ol.js
//= require_self

var MapView = (function() {
    function init() {

        var map = new ol.Map({
            controls: ol.control.defaults().extend([
                new ol.control.ScaleLine({
                    units: 'degrees'
                })
            ]),
            layers: [
                new ol.layer.Group({
                    'title': 'Base Maps',
                    layers: [
                      new ol.layer.Tile({
                          title: 'OSM Bright',
                          type: 'base',
                          source: new ol.source.TileWMS({
                              url: '/service-proxy/wmsProxy',
                              params: {
                                  'LAYERS': "o2-basemap-bright",
                                  'FORMAT': "image/jpeg"
                              }
                          })
                      }),
                      new ol.layer.Tile({
                          title: 'OSM Basic',
                          type: 'base',
                          source: new ol.source.TileWMS({
                              url: '/service-proxy/wmsProxy',
                              params: {
                                  'LAYERS': "o2-basemap-basic",
                                  'FORMAT': "image/jpeg"
                              }
                          })
                      })
                    ]
                })
            ],
            target: 'map',
            view: new ol.View({
                projection: 'EPSG:4326',
                center: [0, 0],
                zoom: 2
            })
        });

        var layerSwitcher = new ol.control.LayerSwitcher();
        map.addControl(layerSwitcher);

    }

    return {
        init: init
    }
})();
