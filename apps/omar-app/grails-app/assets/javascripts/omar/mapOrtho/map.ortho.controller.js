(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('MapOrthoController', ['APP_CONFIG', '$stateParams', '$http', MapOrthoController]);

    function MapOrthoController(APP_CONFIG, $stateParams, $http){

        /* jshint validthis: true */
        var vm = this;

        //vm.title = "Map Ortho";

        console.log('$stateParams', $stateParams);

        var mapOrtho,
            mapOrthoView,
            imageLayers,
            imageLayerIds,
            vectorLayer,
            recommendImageId;

        imageLayerIds = $stateParams.layers;
        imageLayerIds.split(",");

        recommendImageId = imageLayerIds[0]; // grab the first image in query string param
        console.log('recommendId: ', recommendImageId);

        vectorLayer = new ol.layer.Vector({
            opacity: 0.0,
            source: new ol.source.Vector()
        });

        var wfsRequest = {
            typeName: 'omar:raster_entry',
            namespace: 'http://omar.ossim.org',
            version: '1.1.0',
            outputFormat: 'JSON',
            cql: '',
        };

        wfsRequest.cql = 'id in(' + imageLayerIds + ')';

        //var wfsRequestUrl = APP_CONFIG.services.omar.wfsUrl + "?";
        var wfsRequestUrl = '/o2/wfs?';

        var wfsUrl = wfsRequestUrl +
            "service=WFS" +
            "&version=" + wfsRequest.version +
            "&request=GetFeature" +
            "&typeName=" + wfsRequest.typeName +
            "&filter=" + wfsRequest.cql +
            "&outputFormat=" + wfsRequest.outputFormat;

        var url = encodeURI(wfsUrl);

        // Make a call to the WFS service to get the geometry.
        // TODO: Add the metadata to a tab on the image
        $http({
            method: 'GET',
            url: url
        })
        .then(function(response) {

            var data;
            data = response.data.features;
            console.log('mapOrtho WFS data: ', data[0]);

            var imageFeature = new ol.Feature({
                geometry: new ol.geom.MultiPolygon(data[0].geometry.coordinates)
            });

            vectorLayer.getSource().addFeature(imageFeature);

            var featureExtent = imageFeature.getGeometry().getExtent();

            // Moves the map to the extent of the search item
            mapOrtho.getView().fit(featureExtent, mapOrtho.getSize());

        });

        imageLayers = new ol.layer.Tile({
            opacity: 1.0,
            source: new ol.source.TileWMS( {
                url: '/o2/wms?',
                params: {
                    'LAYERS': 'omar:raster_entry', //imageLayerIds,
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

        var coordTemplate = '{y}, {x}';
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

        function getRecommendedImages(){

            var pioUrl = '/o2/predio/getItemRecommendations?item=' + recommendImageId + '&num=20';
            $http({
                method: 'GET',
                url: pioUrl
            })
                .then(function(response) {
                    var data;
                    data = response;  // callback response from Predictive IO service
                    console.log(data);
                    formatRecommendedList(data);
                    //wfsService.executeWfsTrendingThumbs(data);
                });

        }
        getRecommendedImages();

        function formatRecommendedList(data) {

            var wfsImagesList = [];
            data.data.itemScores.filter(function(el){

                console.log(el);
                wfsImagesList.push(el.item);

            });

            var wfsImageString = wfsImagesList.join(",");

            // TODO: Move this $http to the wfs.service.js
            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                outputFormat: 'JSON',
                cql: '',
            };

            wfsRequest.cql = 'id in(' + wfsImageString + ')';

            console.log('wfsRequest.cql: ', wfsRequest.cql);

            var wfsRequestUrl = APP_CONFIG.services.omar.wfsUrl + "?";

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
                console.log('data from wfs', data);
                vm.recommendedImages = data;
            });

        }

    }

}());
