(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('MapOrthoController', ['$scope', '$state', '$stateParams', '$http', MapOrthoController]);

    function MapOrthoController($scope, $state, $stateParams, $http){

        // #################################################################################
        // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It 
        // provides access to various client params in application.yml
        // #################################################################################
        //console.log('AppO2.APP_CONFIG in MapOrthoController: ', AppO2.APP_CONFIG);


        /* jshint validthis: true */
        var vm = this;

        vm.loading = true;

        //vm.title = "Map Ortho";

        //console.log('$stateParams', $stateParams);

        vm.baseServerUrl = AppO2.APP_CONFIG.serverURL;
        console.log('vm.baseServerUrl: ', vm.baseServerUrl);

        var mapOrtho,
            mapOrthoView,
            imageLayers,
            imageLayerIds,
            vectorLayer,
            recommendImageId;

        var wfsRequest = {
            typeName: 'omar:raster_entry',
            namespace: 'http://omar.ossim.org',
            version: '1.1.0',
            outputFormat: 'JSON',
            cql: '',
        };

        imageLayerIds = $stateParams.layers.split(",");

        //console.log('imageLayerIds', imageLayerIds);

        vectorLayer = new ol.layer.Vector({
            opacity: 0.0,
            source: new ol.source.Vector()
        });

        getImageBounds(imageLayerIds);

        function getImageBounds(imageIds){

            wfsRequest.cql = 'id in(' + imageIds + ')';

            //console.log('wfsRequest.cql', wfsRequest.cql);

            //var wfsRequestUrl = '/o2/wfs?';
            var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;

            var wfsUrl = wfsRequestUrl +
                "service=WFS" +
                "&version=" + wfsRequest.version +
                "&request=GetFeature" +
                "&typeName=" + wfsRequest.typeName +
                "&filter=" + wfsRequest.cql +
                "&outputFormat=" + wfsRequest.outputFormat;

            var url = encodeURI(wfsUrl);

            $http({
                method: 'GET',
                url: url
            })
            .then(function(response) {

                var data;
                data = response.data.features;
                //console.log('mapOrtho WFS data: ', data);
                //console.log('data.length', data.length);

                // If there is more than one image we can get the extent
                // of the vectorLayer to set the maps extent
                if (data.length > 1){

                    // Add each image to the vectorLayer
                    angular.forEach(data, function(image){

                        var imageFeature = new ol.Feature({
                            geometry: new ol.geom.MultiPolygon(image.geometry.coordinates)
                        });

                        vectorLayer.getSource().addFeature(imageFeature);

                    });


                    var vectorLayerExtent = vectorLayer.getSource().getExtent();

                    // Sets the map's extent to all of the images in the vectorLayer
                    mapOrtho.getView().fit(vectorLayerExtent, mapOrtho.getSize());

                }
                // If there is only one image we need to use the extent of the feature (image)
                // in the vectorLayer
                else {

                    var imageFeature = new ol.Feature({
                        geometry: new ol.geom.MultiPolygon(data[0].geometry.coordinates)
                    });

                    vectorLayer.getSource().addFeature(imageFeature);

                    var featureExtent = imageFeature.getGeometry().getExtent();

                    // Moves the map to the extent of the one image
                    mapOrtho.getView().fit(featureExtent, mapOrtho.getSize());

                }

            });

        }

        imageLayers = new ol.layer.Tile({
            opacity: 1.0,
            source: new ol.source.TileWMS( {
                //url: '/o2/wms?',
                url: AppO2.APP_CONFIG.params.wms.baseUrl,
                params: {
                    'LAYERS': 'omar:raster_entry',
                    'FILTER' : "in(" + imageLayerIds + ")",
                    'TILED': true,
                    'VERSION': '1.1.1'
                }
            } ),
            name: imageLayerIds
        });

        mapOrthoView = new ol.View({
            center: [0, 0],
            projection: 'EPSG:4326',
            zoom: 2,
            minZoom: 3,
            maxZoom: 18
        });

        var mousePositionControl = new ol.control.MousePosition({
            coordinateFormat: function(coord) {

                // Get DD
                document.getElementById("dd").innerHTML = coord[1].toFixed(6) + ', ' + coord[0].toFixed(6);

                // Get DMS
                var dmsPoint = new GeoPoint(coord[1], coord[0]);
                document.getElementById("dms").innerHTML = dmsPoint.getLonDeg() + ', ' + dmsPoint.getLatDeg();

                // Get MGRS
                var mgrsPoint = mgrs.forward(coord, 5); // 1m accuracy
                document.getElementById("mgrs").innerHTML = mgrsPoint;

                // Get DD
                //return ol.coordinate.format(coord, coordTemplate, 4);

            },
            projection: 'EPSG:4326',
            // comment the following two lines to have the mouse position
            // be placed within the map.
            className: 'custom-mouse-position',
            //target: document.getElementById('mouse-position'),
            undefinedHTML: '&nbsp;'
        });

        var interactions = ol.interaction.defaults({altShiftDragRotate:true});

        mapOrtho = new ol.Map({
            layers:
                [
                    new ol.layer.Group({
                        'title': 'Base maps',
                        layers: [
                            new ol.layer.Tile({
                                title: 'OSM',
                                type: 'base',
                                visible: true,
                                source: new ol.source.OSM()
                            }),
                            imageLayers,
                            vectorLayer
                        ]
                    })
                ],
            controls: ol.control.defaults().extend([
                new ol.control.FullScreen(),
                new ol.control.ScaleLine()
            ]).extend([mousePositionControl]),
            interactions: interactions,
            target: 'mapOrtho',
            view: mapOrthoView
        });

        function getRecommendedImages(imageId){

            //console.log('imageId', imageId);
            var pioUrl = AppO2.APP_CONFIG.params.predio.baseUrl + 'getItemRecommendations?item=' + imageId + '&num=20';
            $http({
                method: 'GET',
                url: pioUrl
            })
            .then(function(response) {
                var data;
                data = response;  // callback response from Predictive IO service
                //console.log(data);
                formatRecommendedList(data);

            });

        }

        vm.pioAppEnabled = AppO2.APP_CONFIG.params.predio.enabled;
        console.log('PIO enabled: ', vm.pioAppEnabled);
        if (vm.pioAppEnabled) {
            
            console.log(vm.pioAppEnabled);
            // first time we will use the the first item in query string param
            getRecommendedImages(imageLayerIds[0]);

        }

        // // first time we will use the the first item in query string param
        // getRecommendedImages(imageLayerIds[0]);

        function formatRecommendedList(data) {

            var wfsImagesList = [];
            data.data.itemScores.filter(function(el){

                //console.log(el);
                wfsImagesList.push(el.item);

            });

            var wfsImageString = wfsImagesList.join(",");

            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                outputFormat: 'JSON',
                cql: '',
            };

            wfsRequest.cql = 'id in(' + wfsImageString + ')';

            //console.log('wfsRequest.cql: ', wfsRequest.cql);

            //var wfsRequestUrl = APP_CONFIG.services.omar.wfsUrl + "?";
            var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl; 

            var wfsUrl = wfsRequestUrl +
                "service=WFS" +
                "&version=" + wfsRequest.version +
                "&request=GetFeature" +
                "&typeName=" + wfsRequest.typeName +
                "&filter=" + wfsRequest.cql +
                "&outputFormat=" + wfsRequest.outputFormat;

            var url = encodeURI(wfsUrl);

            $http({
                method: 'GET',
                url: url
            })
            .then(function(response) {
                var data;
                data = response.data.features;
                //console.log('data from wfs', data);

                vm.recommendedImages = data;
                vm.loading = false;
            });

        }

        vm.switchMapImage = function(id) {

            //console.log(id);

            //Set url parameter for the layer
            $state.transitionTo('mapOrtho', {layers: id}, { notify: false });

            //Update the map parameters with the new image db id
            var params = imageLayers.getSource().getParams();
            //console.log('params: ', params);
            params.FILTER = "in(" + id + ")"
            imageLayers.getSource().updateParams(params);
            //console.log('params: ', params);

            //Execute call to the wfs service to get the bounds
            getImageBounds(id);

            // TODO: Call predio to update the recommended images
            // Need to fix the bug with the image elements not lining up properly on
            // a new call to get new recommendations
            //getRecommendedImages(id);

        };


    }

}());
