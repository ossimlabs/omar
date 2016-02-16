(function(){
    'use strict';
    angular
        .module('swipeApp')
        .controller('SwipeController', ['$location', '$http', '$interval', '$timeout', SwipeController]);

        function SwipeController($location, $http, $interval, $timeout) {

            /* jshint validthis: true */
            var vm = this;

            var map,
                mapView,
                interactions,
                osm,
                layers,
                omar,
                omarParams,
                omar2,
                omar2Params,
                swipe,
                vectorLayer;

            vm.showHeader = true;

            vm.url = 'http://localhost:7272/o2/wms?';

            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                outputFormat: 'JSON',
                cql: '',
            };

            // var urlParams = $location.search();
            // console.log('urlParams.layers', urlParams.layers.split(","));
            // layers = urlParams.layers.split(",");

            vm.layer1 = '20030224172409SI_CARTERRA_0101495AA00000 00100001AA05100091P  GC   UCT';
            vm.layer2 = '20030125151310SI_CARTERRA_0101314MA00000 00100001MA01200021M  GC   UCT';

            function getImageBounds(imageIds){

                //wfsRequest.cql = 'id in(' + imageIds + ')';
                wfsRequest.cql = "strToUpperCase(title) like '%" + imageIds + "%'";

                console.log('wfsRequest.cql', wfsRequest.cql);

                // TODO: Get from passed in parameter
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
                    console.log('map WFS data: ', data);
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

                        console.log('vectorLayer features', vectorLayer.getSource().getFeatures().length);
                        vectorLayer.getSource().clear();
                        console.log('vectorLayer features', vectorLayer.getSource().getFeatures().length);

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
            //getImageBounds(layers);

            osm = new ol.layer.Tile({
                source: new ol.source.OSM(),
                name: 'OSM'
            });

            vectorLayer = new ol.layer.Vector({
                opacity: 0.0,
                source: new ol.source.Vector(),
                name: 'vectorLayer'
            });

            mapView = new ol.View({
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

            // Create a group for overlays. Add the group to the map when it's created
            // but add the overlay layers later
            var overlayGroup = new ol.layer.Group({
                title: 'Overlays',
                layers: [
                ]
            });

            interactions = ol.interaction.defaults({altShiftDragRotate:true});
            map = new ol.Map({
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
                        overlayGroup
                    ],
                controls: ol.control.defaults().extend([
                    new ol.control.FullScreen({
                        source: 'fullscreen'
                    })
                ]).extend([mousePositionControl]),
                interactions: interactions,
                target: 'map',
                view: mapView
            });

            var layerSwitcher = new ol.control.LayerSwitcher({
                tipLabel: 'Layers' // Optional label for button
            });
            map.addControl(layerSwitcher);

            swipe = document.getElementById('swipe');
            
            vm.swapStatus = false;
            vm.swap = function swap(layer1, layer2) {
                
                layers = [layer1, layer2];

                removeLayer1(layer1);
                removeLayer2(layer2);

                vm.layer1 = layers[1];
                vm.layer2 = layers[0];

                addLayer1(layers[1], swap);
                addLayer2(layers[0], swap);
                //getImageBounds(vm.layer1 + ', ' + vm.layer2);

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

            function addLayer1(i, swap) {
                
                console.log('i: ', i);

                if(omar && !swap){

                    var params = omar.getSource().getParams();

                    params.FILTER = "in(" + i + ")";
                    omar.getSource().updateParams(params);

                }
                else {
                    
                    omar = new ol.layer.Tile({
                        title: 'Image 1',
                        opacity: 1.0,
                        source: new ol.source.TileWMS({
                            url: vm.url,
                            params: {
                                'LAYERS': 'omar:raster_entry',
                                //'FILTER' : "in(" + i + ")",
                                //'FILTER': "strToUpperCase(title) like '%20030123124701SI_CARTERRA_0101230MA00000 00100001MA01200017M%'",
                                'FILTER': "strToUpperCase(title) like '%" + i + "%'",
                                'TILED': true,
                                'VERSION': '1.1.1'
                            }
                        }),
                        name: 'layer1'
                    });
                    overlayGroup.getLayers().push(omar);
                    //map.addLayer(omar);

                }
                
            }
            
            function removeLayer1() {
                
                map.removeLayer(omar);

            }

            function addLayer2(i, swap) {
                
                console.log('i: ', i);

                if(omar2 && !swap){
                    console.log('omar2 present...');

                    var params = omar2.getSource().getParams();

                    params.FILTER = "in(" + i + ")";
                    omar2.getSource().updateParams(params);

                }
                else {
                    //var urlParam = "20030224172409SI_CARTERRA_0101495AA00000 00100001AA04800090P"

                    omar2 = new ol.layer.Tile({
                        title: 'Image 2',
                        opacity: 1.0,
                        source: new ol.source.TileWMS({
                            url: vm.url,
                            params: {
                                'LAYERS': 'omar:raster_entry',
                                //'FILTER' : "in(" + i + ")",
                                //'FILTER': "strToUpperCase(title) like '%20030224172409SI_CARTERRA_0101495AA00000 00100001AA04800090P%'",
                                'FILTER': "strToUpperCase(title) like '%" + i + "%'",
                                'TILED': true,
                                'VERSION': '1.1.1'
                            }
                        }),
                        name: 'layer2'
                    });
                    overlayGroup.getLayers().push(omar2);
                    //map.addLayer(omar2);
                    getImageBounds(vm.layer1); // + ', ' + vm.layer2);
                }
                vm.showHeader = false; // hide the header, show the map
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

            vm.flicker = false;
            var onInterval;
            vm.flickerLayer = function() {

                function flickerOn() {
                    
                    omar2.setVisible(false);
                    $timeout(function(){
                        omar2.setVisible(true);
                        console.log('on...');

                    }, 250, false);

                }

                if (vm.flicker === true) {

                    vm.flicker = true;
                    onInterval = $interval(flickerOn, 1000, false);

                }
                else {
                    
                    $interval.cancel(onInterval);
                    vm.flicker = false;
                    console.log('off...');  
                
                }

            };

            vm.imageOpacity1 = 1.0;
            vm.imageOpacity1Change = function(){
                //console.log('changeing...');
                console.log('vm.imageOpacity1', vm.imageOpacity1);
                omar.setOpacity(parseFloat(vm.imageOpacity1));
            };

            vm.imageOpacity2 = 1.0;
            vm.imageOpacity2Change = function(){
                //console.log('changeing...');
                console.log('vm.imageOpacity2', vm.imageOpacity2);
                omar2.setOpacity(parseFloat(vm.imageOpacity2));
            };

        }
    

})();