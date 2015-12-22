(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('mapService', ['APP_CONFIG', 'wfsService', mapService]);

        function mapService (APP_CONFIG, wfsService) {

            // Add the basemap parameters from the applicaiton config file.
            var osmBaseMapUrl = APP_CONFIG.services.basemaps.osm.url;
            var osmBaseMapLayers = APP_CONFIG.services.basemaps.osm.layers;

            // Add the path to OMAR and the footprints URL
            //var omarUrl = APP_CONFIG.services.omar.url;
            //var omarPort = APP_CONFIG.services.omar.port || '80';
            var omarFootprintsUrl = APP_CONFIG.services.omar.footprintsUrl;

            var zoomToLevel = 16;
            var map,
                mapView,
                searchLayerVector, // Used for visualizing the search items map markers polygon boundaries
                wktFormat,
                searchFeatureWkt,
                iconStyle,
                wktStyle;

            iconStyle = new ol.style.Style({
                image: new ol.style.Icon(({
                    anchor: [0.5, 46],
                    anchorXUnits: 'fraction',
                    anchorYUnits: 'pixels',
                    opacity: 0.75,
                    src: APP_CONFIG.misc.icons.greenMarker
                }))
            });

            wktStyle = new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(255, 100, 50, 0.2)'
                }),
                stroke: new ol.style.Stroke({
                    width: 1.5,
                    color: 'rgba(255, 100, 50, 0.6)'
                })
            });

            searchLayerVector = new ol.layer.Vector({
                source: new ol.source.Vector()
            });

            this.mapInit = function(mapParams){

                //console.log('mapParams', mapParams);

                mapView = new ol.View({
                    //center: [lng, lat],
                    //center: [-80.7253178, 28.1174627],
                    center: [0, 0],
                    projection: 'EPSG:4326',
                    zoom: 12,
                    minZoom: 3,
                    maxZoom: 18
                });
                map = new ol.Map({
                    layers: [
                        new ol.layer.Tile({
                            source: new ol.source.TileWMS({
                                url: osmBaseMapUrl,
                                params: {'LAYERS': osmBaseMapLayers, 'TILED': true},
                                serverType: 'geoserver'
                            })
                        }),
                        new ol.layer.Tile({
                            source: new ol.source.TileWMS( {
                                //url: 'http://localhost:8888/omar/wms/footprints?',
                                //url: 'http://localhost:8888/omar/wms/footprints?',
                                //url: omarUrl + ':' + omarPort + omarFootprintsUrl,
                                url: omarFootprintsUrl,
                                params: {
                                    VERSION: '1.1.1',
                                    SRS: 'EPSG:3857',
                                    LAYERS: 'Imagery',
                                    FORMAT: 'image/png',
                                    STYLES: 'byFileType'
                                }
                            } )
                        })
                    ],
                    controls: ol.control.defaults({
                        attributionOptions: ({
                            collapsible: false
                        })
                    }),
                    target: 'map',
                    view: mapView
                });

                map.addLayer(searchLayerVector);

                var mapObj = {};

                map.on('moveend', function(){

                    mapObj.cql = "INTERSECTS(ground_geom," + convertToWktPolygon() + ")";

                    // Update the image cards in the list
                    wfsService.executeWfsQuery(mapObj);

                });

                if (mapParams === undefined) {

                    //zoomTo(0,0,4);
                    zoomTo(33.3116664,44.2858655,4)

                }
                else if (mapParams !== undefined && mapParams.bounds === undefined) {

                    zoomTo(mapParams.lat, mapParams.lng, zoomToLevel, true);

                }
                else {

                    zoomToExt(mapParams);

                }

            };

            this.resizeElement = function (element, height){
                //console.log('resizing');
                $(element).animate({height:$(window).height()- height}, 10, function(){
                    map.updateSize();
                });

            };

            function getMapBbox () {

                return map.getView().calculateExtent(map.getSize());

            };

            function convertToWktPolygon(){
                var extent = getMapBbox();
                var minX = extent[0];
                var minY = extent[1];
                var maxX = extent[2];
                var maxY = extent[3];

                var wkt = "POLYGON((" + minX + " " + minY + ", " + minX + " " + maxY + ", " + maxX + " " + maxY + ", "
                    + maxX + " " + minY + ", " + minX + " " + minY + "))";

                //console.log('wkt', wkt);

                return wkt;

            };

            /**
             * Move and zoom the map to a
             * certain location via a latitude
             * and longitude
             * @function zoomTo
             * @memberof Map
             * @param {number} lat - Latitude
             * @param {number} lon - Longitude
             */
            function zoomTo(lat, lon, zoomLevel, marker) {
                //console.log('zoomTo firing!');
                zoomAnimate();
                map.getView().setCenter([parseFloat(lon), parseFloat(lat)]);
                map.getView().setZoom(zoomLevel);
                if (marker) {
                    addMarker(parseFloat(lat),parseFloat(lon), searchLayerVector);
                }

            }

            /**
             * Move and zoom the map to a
             * certain location via an extent
             * @function zoomToExt
             * @memberof Map
             * @param {obj} inputExtent - inputExtent
             */
            function zoomToExt(inputExtent) {

                clearLayerSource(searchLayerVector);

                var neFeature = new ol.Feature({
                    //geometry: new ol.geom.Point(ol.proj.transform([inputExtent.bounds.ne.lng, inputExtent.bounds.ne.lat],
                    // 'EPSG:4326', 'EPSG:3857'))
                    geometry: new ol.geom.Point([inputExtent.bounds.ne.lng, inputExtent.bounds.ne.lat])
                });
                //console.log('neFeature', inputExtent.bounds.ne.lng + ' ' + inputExtent.bounds.ne.lat);
                var swFeature = new ol.Feature({
                    //geometry: new ol.geom.Point(ol.proj.transform([inputExtent.bounds.sw.lng, inputExtent.bounds.sw.lat],
                    // 'EPSG:4326', 'EPSG:3857'))
                    geometry: new ol.geom.Point([inputExtent.bounds.sw.lng, inputExtent.bounds.sw.lat])
                });
                //console.log('swFeature', inputExtent.bounds.sw.lng + ' ' + inputExtent.bounds.sw.lat);
                searchLayerVector.getSource().addFeatures([neFeature, swFeature]);

                var searchItemExtent = searchLayerVector.getSource().getExtent();

                //zoomAnimate();

                // Moves the map to the extent of the search item
                map.getView().fit(searchItemExtent, map.getSize());

                // Clean up the searchLayer extent for the next query
                searchLayerVector.getSource().clear();

                // Add the WKT to the map to illustrate the boundary of the search item
                if (inputExtent.wkt !== undefined){

                    wktFormat = new ol.format.WKT();
                    // WKT string is in 4326 so we need to reproject it for the current map
                    searchFeatureWkt = wktFormat.readFeature(inputExtent.wkt, {
                        dataProjection: 'EPSG:4326',
                        featureProjection: 'EPSG:4326'
                    });

                    searchFeatureWkt.setStyle(wktStyle);
                    searchLayerVector.getSource().addFeatures([searchFeatureWkt]);

                }
                else {
                    // Add a marker to the map if there isn't a wkt
                    // present with the search item
                    addMarker(inputExtent.lat, inputExtent.lng, searchLayerVector);
                }
            }

            function zoomAnimate(){

                var start = +new Date();
                var pan = ol.animation.pan({
                    duration: 750,
                    source: (map.getView().getCenter()),
                    start: start
                });
                var zoom = ol.animation.zoom({
                    duration: 1000,
                    resolution: map.getView().getResolution()
                });

                map.beforeRender(zoom, pan);
            }

            /**
             * Clear a layer's source, and
             * remove all features
             * @function clearLayerSource
             * @memberof Map
             * @param {layer} layer - layer
             */
            function clearLayerSource(layer){

                if (layer.getSource().getFeatures().length >=1 ){
                    layer.getSource().clear();
                }

            }

            /**
             * Add a marker to the map
             * at a specified point.  Clears
             * previous instance of a maker
             * if they exist before placing
             * a new one
             * @function addMarker
             * @memberof Map
             * @param {number} lat - Latitude
             * @param {number} lon - Longitude
             * @param {layer} layer - layer
             */
            function addMarker(lat, lon, layer){

                clearLayerSource(layer);
                var centerFeature = new ol.Feature({
                    geometry: new ol.geom.Point([parseFloat(lon), parseFloat(lat)])
                });
                centerFeature.setStyle(iconStyle);
                layer.getSource().addFeatures([centerFeature]);
            }

        }

}());

