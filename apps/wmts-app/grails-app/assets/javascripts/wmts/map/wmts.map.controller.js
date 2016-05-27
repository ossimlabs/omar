(function () {
  'use strict';

  angular
    .module('wmtsApp')
    .controller('WmtsMapController', ['$http', 'toastr', WmtsMapController]);

      function WmtsMapController ($http, toastr) {

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

      var overlayGroup = new ol.layer.Group({
          title: 'WMTS Layers',
          layers: [

          ]
      });

      var baseMaps = new ol.layer.Group({
          'title': 'Base maps',
          layers: [
              new ol.layer.Tile({
                  title: 'OSM',
                  //type: 'base',
                  visible: true,
                  source: new ol.source.OSM()
              }),
              new ol.layer.Tile({
                  title: 'Roads',
                  //type: 'base',
                  visible: false,
                  source: new ol.source.MapQuest({layer: 'osm'})
              }),
              new ol.layer.Tile({
                  title: 'Satellite',
                  //type: 'base',
                  visible: false,
                  source: new ol.source.MapQuest({layer: 'sat'})
              }),
              new ol.layer.Tile({
                title: 'Image Footprints',
                visible: true,
                source: new ol.source.TileWMS({
                  // TODO: The filter for the footprints needs to match
                  //       that of the WMTS layer
                  url: "http://o2.ossim.org/o2/footprints/getFootprints",
                  params: {
                      FILTER: "",
                      VERSION: '1.1.1',
                      LAYERS: 'omar:raster_entry',
                      STYLES: 'byFileType'
                  }
                })
              })
          ]
      });

      var layers = [
        baseMaps,
        overlayGroup
      ];

      var interactions = ol.interaction.defaults({altShiftDragRotate:true});
      var map = new ol.Map({
        controls: ol.control.defaults().extend( [
            new ol.control.FullScreen()
        ]),
        interaction: interactions,
        layers: layers,
        target: 'map',
        view: new ol.View({
          center: [114.1609699, 22.277157],
          projection: 'EPSG:4326',
          zoom: 11,
          minZoom: 2
        })
      });

      var layerSwitcher = new ol.control.LayerSwitcher({
          tipLabel: 'Layers' // Optional label for button
      });
      map.addControl(layerSwitcher);

      // TODO: The url will need to come from the env. var passed in from the .yml
      var url = 'http://localhost:8080/wmts/layers';

      // Pulls in the list of WMTS layers from the WMTS API
      function getWmtsLayersList () {
        $http({
          method: 'GET',
          url: url
        })
        .then(function(response){

          //Adds toastr banner on initial load of app
          toastr.info("Welcome to the O2 WMTS Viewer", 'Information:', {
              closeButton: true,
              timeOut: 10000,
              extendedTimeOut: 5000,
          });

          //console.log('response.data.results', response.data.results);

          function addLayerToSwitcher(layer){

            // console.log('layer.name', layer.name);
            createWmtsOlLayer(layer.name);

            // TODO: Update the filter on the footprints layer?
            // console.log('layer.filter', layer.filter);

          }

          // Adds the layers to the layerSwitcher control
          var layers = response.data.results.map(addLayerToSwitcher);

        })
        .catch(function(e){

          console.log(e);
          toastr.error("Sorry, there was an problem retrieving the WMTS Layers.", 'Error:', {
                closeButton: true,
                timeOut: 10000,
                extendedTimeOut: 5000,
          });

        });
      }
      getWmtsLayersList();

      function createWmtsOlLayer (layerName){

        console.log('createWmtsOlLayer => layerName', layerName);
        // Create a new WMTS tile layer
        var wmtsLayer = new ol.layer.Tile({
          title: layerName,
          type: 'base',
          visible: true,
          opacity: 1.0,
          source: new ol.source.WMTS({
            // TODO: This needs to be passed in as an env. variable
            url: 'http://localhost:8080/wmts',
            layer: layerName,
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
        });
        console.log('wmtsLayer: ', wmtsLayer);
        overlayGroup.getLayers().push(wmtsLayer);

      }

    } // end WmtsMapController

})();
