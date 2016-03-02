(function(){
    'use strict';
    angular
        .module('swipeApp')
        .controller('SwipeController', ['$location', '$http', SwipeController]);

        function SwipeController($location, $http) {

            /* jshint validthis: true */
            var vm = this;

            var map,
                mapView,
                interactions,
                osm,
                layers,
                omar,
                omar2,
                swipe,
                vectorLayer;

            vm.showHeader = false;

            vm.url = 'http://localhost:7272/o2/wms?';

            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                outputFormat: 'JSON',
                cql: '',
            };

            var urlParams = $location.search();
            console.log('urlParams.layers', urlParams.layers.split(","));
            layers = urlParams.layers.split(",");

            vm.layer1 = layers[0];
            vm.layer2 = layers[1];

            function getImageBounds(imageIds){

                wfsRequest.cql = 'id in(' + imageIds + ')';

                console.log('wfsRequest.cql', wfsRequest.cql);

                var wfsRequestUrl = 'http://localhost:7272/o2/wfs?';

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
                        //console.log('map WFS data: ', data);
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
                            map.getView().fit(vectorLayerExtent, map.getSize());

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
                            map.getView().fit(featureExtent, map.getSize());

                        }

                    });

            }
            getImageBounds(layers);

            osm = new ol.layer.Tile({
                source: new ol.source.OSM()
            });

            vectorLayer = new ol.layer.Vector({
                opacity: 0.0,
                source: new ol.source.Vector()
            });

            mapView = new ol.View({
                center: [0, 0],
                projection: 'EPSG:4326',
                zoom: 2,
                minZoom: 3,
                maxZoom: 18
            });

            interactions = ol.interaction.defaults({altShiftDragRotate:true});
            map = new ol.Map({
                controls: ol.control.defaults().extend([
                    new ol.control.FullScreen({
                        source: 'fullscreen'
                    })
                ]),
                interactions: interactions,
                layers: [osm],
                target: 'map',
                view: mapView
            });

            swipe = document.getElementById('swipe');

            addLayer1(layers[0]);
            addLayer2(layers[1]);

            vm.swap = function swap(layer1, layer2) {
                layers = [layer1, layer2];

                removeLayer1(layer1);
                removeLayer2(layer2);

                this.layer1 = layers[1];
                this.layer2 = layers[0];

                addLayer1(layers[1]);
                addLayer2(layers[0]);

            };

            vm.addLayer1 = function add1(layer) {
                addLayer1(layer);
            };
            vm.removeLayer1 = function remove1(layer) {
                removeLayer1(layer);
            };

            vm.addLayer2 = function add2(layer) {
                addLayer2(layer);
            };
            vm.removeLayer2 = function remove2(layer) {
                removeLayer2(layer);
            };

            function addLayer1(i) {
                console.log('addLayer1 firing...');
                omar = new ol.layer.Tile({
                    opacity: 1.0,
                    source: new ol.source.TileWMS({
                        url: vm.url,
                        params: {
                            'LAYERS': 'omar:raster_entry',
                            'FILTER' : "in(" + i + ")",
                            'TILED': true,
                            'VERSION': '1.1.1'
                        }
                    })
                });

                map.addLayer(omar);
            }
            
            function removeLayer1() {
                
                map.removeLayer(omar);

            }

            function addLayer2(i) {
                console.log('addLayer2 firing...');
                omar2 = new ol.layer.Tile({
                    opacity: 1.0,
                    source: new ol.source.TileWMS({
                        url: vm.url,
                        params: {
                            'LAYERS': 'omar:raster_entry',
                            'FILTER' : "in(" + i + ")",
                            'TILED': true,
                            'VERSION': '1.1.1'
                        }
                    })
                });


                map.addLayer(omar2);
                setSwipe();
            }
            
            function removeLayer2() {

                map.removeLayer(omar2);

            }


            function setSwipe() {

                omar2.on('precompose', function (event) {
                    var ctx = event.context;
                    var width = ctx.canvas.width * (swipe.value / 100);

                    ctx.save();
                    ctx.beginPath();
                    ctx.rect(width, 0, ctx.canvas.width - width, ctx.canvas.height);
                    ctx.clip();
                });

                omar2.on('postcompose', function (event) {
                    var ctx = event.context;
                    ctx.restore();
                });

                swipe.addEventListener('input', function () {
                    map.render();
                }, false);

            }

        }

})();