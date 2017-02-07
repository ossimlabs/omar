(function(){
    'use strict';
    angular
        .module('swipeApp')
        .controller('SwipeController', ['$location', '$http', '$interval', '$timeout', '$scope', SwipeController]);

        function SwipeController($location, $http, $interval, $timeout, $scope) {
            
            // #################################################################################
            // AppSwipe.APP_CONFIG is passed down from the .gsp, and is a global variable.  It 
            // provides access to various client params in application.yml
            // #################################################################################
            //console.log('AppSwipe.APP_CONFIG in SwipeController: ', AppSwipe.APP_CONFIG);
            
            /* jshint validthis: true */
            var vm = this;

            var map,
                mapView,
                interactions,
                layers,
                overlayGroup,
                omar,
                omar2,
                swipe,
                vectorLayer;

            vm.showHeader = true;

            vm.url = APP_CONFIG.clientParams.wms.baseUrl;

            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                outputFormat: 'JSON',
                cql: '',
            };

            //vm.layer1 = '20030205151053SI_CARTERRA_0101684MA00000 00100001MA01100011M  GC   UCT';
            //vm.layer2 = '20030205151053SI_CARTERRA_0101684MA00000 00100001MA01100011M  GC   UCT';

            vm.layer1 = '20030224172409SI_CARTERRA_0101495AA00000 00100001AA05100091P  GC   UCT';
            vm.layer2 = '20030125151310SI_CARTERRA_0101314MA00000 00100001MA01200021M  GC   UCT';

            vm.vectorLayerExtent = [];
            function getImageExtents(imageIds){

                var imageIdsArray = imageIds.split(',');

                wfsRequest.cql = "title in('" + imageIdsArray[0] + "'," + "'" + imageIdsArray[1] + "')";

                var wfsRequestUrl = APP_CONFIG.clientParams.wfs.baseUrl;

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

                    //vectorLayer.getSource().clear();
                    vectorLayer.getSource().clear();

                    var data;
                    data = response.data.features;
                    var geojsonExtentsArray  = [];
                    var intersection;
                    var format = new ol.format.GeoJSON();

                    // If there is more than one image we can get the extent
                    // of the vectorLayer to set the maps extent
                    if (data.length >= 1){

                        // Add each image to the vectorLayer
                        angular.forEach(data, function(image){

                            var imageFeature = new ol.Feature({
                                geometry: new ol.geom.MultiPolygon(image.geometry.coordinates)
                            });

                            //imageFeature.setStyle(extentStyle);
                            vectorLayer.getSource().addFeature(imageFeature);

                            // Calculate intersection of image boundaries
                            var polygonCoordinates = imageFeature.getGeometry().getPolygon(0).getCoordinates();

                            var polygonFeature = new ol.Feature({
                                geometry: new ol.geom.Polygon(polygonCoordinates)
                            });
                            
                            geojsonExtentsArray.push(polygonFeature);
                        

                        });

                        vm.vectorLayerExtent = vectorLayer.getSource().getExtent();

                        // Sets the map's extent to all of the images in the vectorLayer
                        map.getView().fit(vm.vectorLayerExtent, map.getSize());

                        vectorLayer.getSource().clear();

                        intersection = turf.intersect(
                            format.writeFeatureObject(geojsonExtentsArray[0]),
                            format.writeFeatureObject(geojsonExtentsArray[1])
                        );
                        //console.log('intersection value', intersection);

                        var intersectionFeature;
                        if (intersection !== undefined) {

                            intersectionFeature = format.readFeature(
                                intersection
                            );
                            intersectionFeature.setStyle(extentStyle);   
                            vectorLayer.getSource().addFeature(intersectionFeature);

                        }
                        else {

                            alert('Images do not intersect!');

                        }
                

                    }
                    else {
                        
                        alert('Error, could not find one of the images!');

                    }

                });

            }

            vectorLayer = new ol.layer.Vector({
                title: 'Image Interaction',
                opacity: 1.0,
                source: new ol.source.Vector(),
                name: 'vectorLayer'
            });

            var extentStyle = new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(255, 100, 50, 0.6)'
            }),
                stroke: new ol.style.Stroke({
                    width: 1.5,
                    color: 'rgba(255, 100, 50, 0.8)'
                })
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

            overlayGroup = new ol.layer.Group({
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
            
            //vm.swapStatus = false;
            vm.swap = function swap(layer1, layer2) {
                
                layers = [layer1, layer2];

                removeLayer1(layer1);
                removeLayer2(layer2);

                vm.layer1 = layers[1];
                vm.layer2 = layers[0];
                
                vm.addLayer1(layers[1], false, true);
                vm.addLayer2(layers[0], false, true);

            };

            vm.addLayer1 = function(i, refresh, swap) {

                if(omar && !refresh){
                    
                    if (swap === undefined) {

                        // Now we need to update the extent
                        getImageExtents(vm.layer1 + ',' + vm.layer2);
                        swap === undefined;
                    }
                    
                    // Add a slight pause so that we don't make the WMS request
                    // from the intial extent of the map.  This is sufficient
                    // enough time to wait for the call to the WFS to get the
                    // extents of the images.
                    $timeout(function(){
                        omar.setExtent(vm.vectorLayerExtent);

                        var params = omar.getSource().getParams();
                        params.FILTER = "title in('" + i + "')";
                        omar.getSource().updateParams(params);

                        vm.addLayer2(vm.layer2, false);
                        
                    }, 1000);

                }
                else {

                    // Request the extent for the images so that we can restrict
                    // the WMS request to just that area.
                    getImageExtents(vm.layer1 + ',' + vm.layer2);
                    
                    // Add a slight pause so that we don't make the WMS request
                    // from the intial extent of the map.  This is sufficient
                    // enough time to wait for the call to the WFS to get the
                    // extents of the images.
                    $timeout(function(){
                        
                        omar = new ol.layer.Tile({
                            title: 'Image 1',
                            opacity: 1.0,
                            extent: vm.vectorLayerExtent,
                            source: new ol.source.TileWMS({
                                url: vm.url,
                                params: {
                                    'LAYERS': 'omar:raster_entry',
                                    'FILTER': "title like '%" + i + "%'",
                                    'TILED': true,
                                    'VERSION': '1.1.1'
                                }
                            }),
                            name: 'layer1'
                        });

                        overlayGroup.getLayers().push(omar);
                        
                        vm.addLayer2(vm.layer2, false);
                        
                    }, 1000, false);

                }
                
            };
            
            function removeLayer1() {
                
                map.removeLayer(omar);

            }

            vm.addLayer2 = function(i, refresh) {
  
                if(omar2 && !refresh){

                    // Now we need to update the extent
                    omar2.setExtent(vm.vectorLayerExtent);

                    var params = omar2.getSource().getParams();
                    params.FILTER = "title in('" + i + "')";
                    omar2.getSource().updateParams(params);

                }
                else {

                    omar2 = new ol.layer.Tile({
                        title: 'Image 2',
                        opacity: 1.0,
                        extent: vm.vectorLayerExtent,
                        source: new ol.source.TileWMS({
                            url: vm.url,
                            params: {
                                'LAYERS': 'omar:raster_entry',
                                'FILTER': "title like '%" + i + "%'",
                                'TILED': true,
                                'VERSION': '1.1.1'
                            }
                        }),
                        name: 'layer2'
                    });
                    overlayGroup.getLayers().push(omar2);
                    overlayGroup.getLayers().push(vectorLayer);
                    vectorLayer.setVisible(false);
                    
                    setSwipe();
                    
                    // Don't try to remove the header image if it has already
                    // been removed with the initial submit
                    if (vm.showHeader) {
                        $scope.$apply(function(){
                            
                            // hide the header, show the map
                            vm.showHeader = false; 

                        });
                    }
                    
                }           
                
            };
            
            function removeLayer2() {

                map.removeLayer(omar2);

            }

            vm.resetLayers = function() {

                // Clear out the image input form elements
                vm.layer1 = '';
                vm.layer2 = '';
                
            };

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
                    $timeout(function() {
                        
                        omar2.setVisible(true);

                    }, 250, false);

                }

                if (vm.flicker === true) {

                    vm.flicker = true;
                    onInterval = $interval(flickerOn, 1000, false);

                }
                else {
                    
                    $interval.cancel(onInterval);
                    vm.flicker = false;  
                
                }

            };

            vm.imageOpacity1 = 1.0;
            vm.imageOpacity1Change = function() {
                
                omar.setOpacity(parseFloat(vm.imageOpacity1));

            };

            vm.imageOpacity2 = 1.0;
            vm.imageOpacity2Change = function() {
                
                omar2.setOpacity(parseFloat(vm.imageOpacity2));

            };

        }
    

})();