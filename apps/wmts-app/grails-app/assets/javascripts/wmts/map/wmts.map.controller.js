(function() {
  'use strict';

  angular
    .module('wmtsApp')
    .controller('WmtsMapController', ['toastr', WmtsMapController]);

    function WmtsMapController (toastr) {

      toastr.info("Welcome to the O2 WMTS Viewer", 'Information:', {
          positionClass: 'toast-bottom-left',
          closeButton: true,
          timeOut: 10000,
          extendedTimeOut: 5000,
          target: 'body'
      });

      var projection = ol.proj.get('EPSG:4326');
      // var projection = ol.proj.get('EPSG:3857');
      var projectionExtent = projection.getExtent();
      var size = (ol.extent.getWidth(projectionExtent)*0.5) / 256;
      //var size = (ol.extent.getWidth(projectionExtent)) / 256;
      var resolutions = new Array(20);
      var matrixIds = new Array(20);
      for (var z = 0; z < 20; ++z) {
        // generate resolutions and matrixIds arrays for this WMTS
        resolutions[z] = size / Math.pow(2, z);
        matrixIds[z] = z;
      }

      var attribution = new ol.Attribution({
        html: 'Tiles &copy; <a href="http://services.arcgisonline.com/arcgis/rest/' +
            'services/Demographics/USA_Population_Density/MapServer/">ArcGIS</a>'
      });


        var layers = [
            // new ol.layer.Tile({
            //   source: new ol.source.OSM(),
            //   opacity: 0.7
            // }),
          new ol.layer.Tile({
            source: new ol.source.TileWMS({
              url: 'http://demo.boundlessgeo.com/geoserver/wms',
              params: {
                'LAYERS': 'ne:NE1_HR_LC_SR_W_DR'
              }
            })
          }),

          new ol.layer.Tile({
          opacity: 1.0,
          source: new ol.source.WMTS({
            attributions: [attribution],
    //        url: 'http://o2.ossim.org/wmts-app/wmts',
            url: 'http://localhost:8080/wmts',
    //        url: 'http://192.168.2.101:8080/wmts',
    //        url: 'http://localhost:8080/wmts',
           //layer: 'WorldGeographic',
           layer: 'Foo',
            // layer: 'WorldMercator',
            matrixSet: 'WorldGeographic',
            // matrixSet: 'WorldMercator',
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
      }),

    new ol.layer.Tile({
      title: 'Image Footprints',
      source: new ol.source.TileWMS({
          url: "http://o2.ossim.org/o2/footprints/getFootprints",
//                   url: "http://o2.ossim.org/o2/footprints/getFootprints",
//                   url: "http://localhost:8080/omar/wms/footprints",
          params: {
              FILTER: "",
              VERSION: '1.1.1',
              LAYERS: 'omar:raster_entry',
//                        LAYERS: 'Imagery',
              STYLES: 'byFileType'
          }
      })
    })
  ];



      var map = new ol.Map({
        // layers: [
        //   new ol.layer.Tile({
        //     source: new ol.source.OSM(),
        //     opacity: 0.7
        //   }),
          // new ol.layer.Tile({
          //   opacity: 0.7,
          //   source: new ol.source.WMTS({
          //     attributions: 'Tiles © <a href="http://services.arcgisonline.com/arcgis/rest/' +
          //         'services/Demographics/USA_Population_Density/MapServer/">ArcGIS</a>',
          //     url: 'http://services.arcgisonline.com/arcgis/rest/' +
          //         'services/Demographics/USA_Population_Density/MapServer/WMTS/',
          //     layer: '0',
          //     matrixSet: 'EPSG:3857',
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
          // })
        //],
        layers: layers,
        target: 'map',
        controls: ol.control.defaults({
          attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
            collapsible: false
          })
        }),
        view: new ol.View({
          center: [-11158582, 4813697],
          zoom: 4
        })
      });

      console.log('map', map);
    }

})();
