(function(){
    'use strict';
    angular
        .module('swipeApp')
        .controller('SwipeController', ['$location', SwipeController]);

        function SwipeController($location) {

            /* jshint validthis: true */
            var vm = this;

            var map,
                mapView,
                osm,
                layers,
                omar,
                omar2,
                swipe;

            vm.url = 'http://localhost:7272/o2/wms?';

            vm.layer1 = 69;
            vm.layer2 = 76;

            var urlParams = $location.search();
            console.log('urlParams.layers', urlParams.layers.split(","));
            layers = urlParams.layers.split(",");


            console.log('layers[0]', layers[0]);







            osm = new ol.layer.Tile({
                source: new ol.source.OSM()
            });

            mapView = new ol.View({
                center: [0, 0],
                projection: 'EPSG:4326',
                zoom: 2,
                minZoom: 3,
                maxZoom: 18
            });

            map = new ol.Map({
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
                omar = new ol.layer.Tile({
                    opacity: 1.0,
                    source: new ol.source.TileWMS({
                        url: vm.url,
                        params: {
                            //'LAYERS': i,
                            'LAYERS': 'omar:raster_entry',
                            'FILTER' : "in(" + i + ")",
                            'TILED': true,
                            'VERSION': '1.1.1'
                        }
                    })
                });

                map.addLayer(omar);
            }
            function removeLayer1(i) {
                map.removeLayer(omar);
            }

            function addLayer2(i) {
                omar2 = new ol.layer.Tile({
                    opacity: 1.0,
                    source: new ol.source.TileWMS({
                        url: vm.url,
                        params: {
                            //'LAYERS': i,
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
            function removeLayer2(i) {

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