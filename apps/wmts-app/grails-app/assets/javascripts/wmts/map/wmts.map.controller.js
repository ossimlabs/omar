(function() {
  'use strict';

  angular
    .module('wmtsApp')
    .controller('WmtsMapController', ['toastr', WmtsMapController]);

    function WmtsMapController (toastr) {

      // Adds toastr banner on initial load of app.
      toastr.info("Welcome to the O2 WMTS Viewer", 'Information:', {
          positionClass: 'toast-bottom-left',
          closeButton: true,
          timeOut: 10000,
          extendedTimeOut: 5000,
          target: 'body'
      });

      var projection = ol.proj.get('EPSG:4326');
      var projectionExtent = projection.getExtent();
      var size = (ol.extent.getWidth(projectionExtent)*0.5) / 256;
      var resolutions = new Array(20);
      var matrixIds = new Array(20);
      for (var z = 0; z < 20; ++z) {
        // generate resolutions and matrixIds arrays for this WMTS
        resolutions[z] = size / Math.pow(2, z);
        matrixIds[z] = z;
      }

      var layers = [
        new ol.layer.Group({
            'title': 'Base maps',
            layers: [
                new ol.layer.Tile({
                    title: 'OSM',
                    type: 'base',
                    visible: true,
                    source: new ol.source.OSM()
                }),
                new ol.layer.Tile({
                    title: 'Roads',
                    type: 'base',
                    visible: false,
                    source: new ol.source.MapQuest({layer: 'osm'})
                }),
                new ol.layer.Tile({
                    title: 'Satellite',
                    type: 'base',
                    visible: false,
                    source: new ol.source.MapQuest({layer: 'sat'})
                })
            ]
        }),
        // new ol.layer.Tile({
        //   opacity: 1.0,
        //   source: new ol.source.WMTS({
        //     //attributions: [attribution],
        //     // url: 'http://o2.ossim.org/wmts-app/wmts',
        //     url: 'http://localhost:8080/wmts',
        //     // url: 'http://192.168.2.101:8080/wmts',
        //     // url: 'http://localhost:8080/wmts',
        //     // layer: 'WorldGeographic',
        //     layer: 'Foo',
        //     matrixSet: 'WorldGeographic',
        //     format: 'image/png',
        //     projection: projection,
        //     tileGrid: new ol.tilegrid.WMTS({
        //       origin: ol.extent.getTopLeft(projectionExtent),
        //       resolutions: resolutions,
        //       matrixIds: matrixIds
        //     }),
        //     style: 'default',
        //     wrapX: true
        //   })
        // }),
        new ol.layer.Group({
            title: 'Overlays',
            layers: [
              new ol.layer.Tile({
                title: 'Image Footprints',
                source: new ol.source.TileWMS({
                  url: "http://o2.ossim.org/o2/footprints/getFootprints",
                  // url: "http://localhost:8080/omar/wms/footprints",
                  params: {
                      FILTER: "",
                      VERSION: '1.1.1',
                      LAYERS: 'omar:raster_entry',
                      STYLES: 'byFileType'
                  }
                })
              }),
              new ol.layer.Tile({
                title: 'Foo',
                opacity: 1.0,
                source: new ol.source.WMTS({
                  //attributions: [attribution],
                  // url: 'http://o2.ossim.org/wmts-app/wmts',
                  url: 'http://localhost:8080/wmts',
                  // url: 'http://192.168.2.101:8080/wmts',
                  // url: 'http://localhost:8080/wmts',
                  //layer: 'WorldGeographic',
                  layer: 'Foo',
                  matrixSet: 'WorldGeographic',
                  format: 'image/png',
                  projection: projection,
                  tileGrid: new ol.tilegrid.WMTS({
                    origin: ol.extent.getTopLeft(projectionExtent),
                    resolutions: resolutions,
                    matrixIds: matrixIds
                  }),
                  style: 'default',
                  wrapX: true
                })
              })
            ]
        })
      ];

      var map = new ol.Map({
        layers: layers,
        target: 'map',
        view: new ol.View({
          center: [0, 0],
          projection: 'EPSG:4326',
          zoom: 2,
          minZoom: 2,
          maxZoom: 18
        })
      });

      var layerSwitcher = new ol.control.LayerSwitcher({
          tipLabel: 'Layers' // Optional label for button
      });
      map.addControl(layerSwitcher);

    } // end WmtsMapController

})();
