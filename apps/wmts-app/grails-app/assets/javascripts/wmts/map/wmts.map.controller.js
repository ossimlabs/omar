(function () {
  'use strict';

  angular
    .module('wmtsApp')
    .controller('WmtsMapController', ['$http', 'toastr', WmtsMapController]);

      function WmtsMapController ($http, toastr) {

        // #################################################################################
        // AppWmts.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
        // provides access to various client params in application.yml
        // #################################################################################
        console.log('AppWmts.APP_CONFIG in wmts.map.controller: ', AppWmts.APP_CONFIG);


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

        var footprints = new ol.layer.Tile({
          title: 'Image Footprints',
          visible: true,
          source: new ol.source.TileWMS({
            // TODO: The filter for the footprints needs to match
            //       that of the WMTS layer
            url: AppWmts.APP_CONFIG.wmts.footprints.url,
            params: {
                FILTER: "",
                VERSION: '1.1.1',
                LAYERS: AppWmts.APP_CONFIG.wmts.footprints.layers,
                STYLES: AppWmts.APP_CONFIG.wmts.footprints.styles
            }
          })
        });

        var overlayGroup = new ol.layer.Group({
            title: 'WMTS Layers',
            layers: []
        });

        var baseMaps = new ol.layer.Group({
            'title': 'Base maps',
            layers: []
        });

        // Takes a map layer obj, and adds
        // the layer to the map layers array.
        function addBaseMapLayers(layerObj){
          var baseMapLayer;
          if (layerObj.layerType.toLowerCase() === 'tilewms'){

            baseMapLayer = new ol.layer.Tile({
                title: layerObj.title,
                type: 'base',
                visible: layerObj.options.visible,
                source: new ol.source.TileWMS({
                    url: layerObj.url,
                    params: {
                       'VERSION': '1.1.1',
                       'LAYERS': layerObj.params.layers,
                       'FORMAT': layerObj.params.format
                   }
               }),
                name: layerObj.title
            });

          }

          if (baseMapLayer != null) {
            // Add layer(s) to the layerSwitcher control
            baseMaps.getLayers().push(baseMapLayer);

          }

        }

        // Map over each layer item in the baseMaps array
        AppWmts.APP_CONFIG.openlayers.baseMaps.map(addBaseMapLayers);

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
            center: [0,0],
            projection: 'EPSG:4326',
            zoom: 2,
            minZoom: 2
          })
        });

        var layerSwitcher = new ol.control.LayerSwitcher({
            tipLabel: 'Layers' // Optional label for button
        });
        map.addControl(layerSwitcher);

        // TODO: The url will need to come from the env. var passed in from the .yml
        //var url = 'http://localhost:8080/wmts/layers';
        var url = AppWmts.APP_CONFIG.serverURL + '/wmts/layers';


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
            var layers = response.data.data.map(addLayerToSwitcher);

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

          // Create a new WMTS tile layer
          var wmtsLayer = new ol.layer.Tile({
            title: layerName,
            //type: 'base',
            visible: true,
            opacity: 1.0,
            source: new ol.source.WMTS({
              // TODO: This needs to be passed in as an env. variable
              url: AppWmts.APP_CONFIG.wmtsURL,
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

          overlayGroup.getLayers().push(wmtsLayer);
          overlayGroup.getLayers().push(footprints);

        }

    } // end WmtsMapController

})();
