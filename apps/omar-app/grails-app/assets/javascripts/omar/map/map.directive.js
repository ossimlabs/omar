(function(){
    'use strict';
    angular
        .module('omarApp')
        .directive('map', map);

        function map(mapService) {
            return {
                restrict: 'A',
                scope: {
                    params: '=',
                },
                link: function(scope, element, attrs) {

                    //mapService.mapServiceTest();

                    //mapService.mapInit(attrs.id, scope.params.lng, scope.params.lat);

                    //var map = new ol.Map({
                    //    target: attrs.id,
                    //    layers: [
                    //        //new ol.layer.Tile({
                    //        //    source: new ol.source.OSM()
                    //        //}),
                    //        new ol.layer.Tile({
                    //            source: new ol.source.TileWMS({
                    //                url: 'http://geoserver-demo01.dev.ossim.org/geoserver/ged/wms?',
                    //                params: {'LAYERS': 'osm-group', 'TILED': true},
                    //                serverType: 'geoserver'
                    //            })
                    //        }),
                    //        //new ol.layer.Tile({
                    //        //    //extent: [-13884991, 2870341, -7455066, 6338219],
                    //        //    source: new ol.source.TileWMS({
                    //        //        url: 'http://demo.boundlessgeo.com/geoserver/wms',
                    //        //        params: {'LAYERS': 'topp:states', 'TILED': true},
                    //        //        serverType: 'geoserver'
                    //        //    })
                    //        //}),
                    //        //new ol.layer.Tile({
                    //        //    source: new ol.source.TileWMS({
                    //        //        url: 'http://demo.boundlessgeo.com/geoserver/wms',
                    //        //        params: {
                    //        //            'LAYERS': 'ne:NE1_HR_LC_SR_W_DR'
                    //        //        }
                    //        //    })
                    //        //}),
                    //        //new ol.layer.Tile({
                    //        //    source: new ol.source.TileWMS({
                    //        //        url: 'http://10.0.10.186:8888/omar/wms/footprints?',
                    //        //        params: {
                    //        //            VERSION: '1.1.1',
                    //        //            LAYERS: 'Imagery',
                    //        //            SRS: 'EPSG:4326',
                    //        //            FORMAT: 'image/png',
                    //        //            STYLES: "byFileType",
                    //        //            TRANSPARENT: true,
                    //        //            TILED: true
                    //        //        },
                    //        //    })
                    //        //})
                    //        new ol.layer.Tile( {
                    //            source: new ol.source.TileWMS( {
                    //                url: 'http://localhost:8888/omar/wms/footprints?',
                    //                params: {
                    //                    VERSION: '1.1.1',
                    //                    SRS: 'EPSG:3857',
                    //                    LAYERS: 'Imagery',
                    //                    FORMAT: 'image/png',
                    //                    STYLES: 'byFileType'
                    //                }
                    //            } )
                    //        } )
                    //    ],
                    //    view: new ol.View({
                    //        //center: ol.proj.fromLonLat([-80.7253178,28.1174627]),
                    //        projection: 'EPSG:4326',
                    //        //center: [-80.7253178, 28.1174627],
                    //        center: [scope.jedi.lng, scope.jedi.lat],
                    //        zoom: 16
                    //    })
                    //});

                    var zoomToLevel = 10;
                    var map,
                        mapView,
                        searchLayerVector, // Used for visualizing the search items map markers polygon boundaries
                        wktFormat,
                        searchFeatureWkt,
                        iconStyle,
                        wktStyle;

                    //iconStyle = new ol.style.Style({
                    //    image: new ol.style.Icon(({
                    //        anchor: [0.5, 46],
                    //        anchorXUnits: 'fraction',
                    //        anchorYUnits: 'pixels',
                    //        opacity: 0.75,
                    //        src: 'assets/search_marker_green.png'
                    //    }))
                    //});

                    //wktStyle = new ol.style.Style({
                    //    fill: new ol.style.Fill({
                    //        color: 'rgba(255, 100, 50, 0.2)'
                    //    }),
                    //    stroke: new ol.style.Stroke({
                    //        width: 1.5,
                    //        color: 'rgba(255, 100, 50, 0.6)'
                    //    })
                    //});

                    //searchLayerVector = new ol.layer.Vector({
                    //    source: new ol.source.Vector()
                    //});

                    /**
                     * Clear a layer's source, and
                     * remove all features
                     * @function clearLayerSource
                     * @memberof Map
                     * @param {layer} layer - layer
                     */
                    //function clearLayerSource(layer){
                    //
                    //    if (layer.getSource().getFeatures().length >=1 ){
                    //        layer.getSource().clear();
                    //    }
                    //
                    //}

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
                    //function addMarker(lat, lon, layer){
                    //
                    //    clearLayerSource(layer);
                    //    var centerFeature = new ol.Feature({
                    //        geometry: new ol.geom.Point(ol.proj.transform([parseFloat(lon), parseFloat(lat)], 'EPSG:4326', 'EPSG:3857'))
                    //    });
                    //    centerFeature.setStyle(iconStyle);
                    //    layer.getSource().addFeatures([centerFeature]);
                    //}

                    /**
                     * Animates the pan and zoom for
                     * the map
                     * @function zoomAnimate
                     * @memberof Map
                     */
                    //function zoomAnimate(){
                    //
                    //    var start = +new Date();
                    //    var pan = ol.animation.pan({
                    //        duration: 750,
                    //        source: (map.getView().getCenter()),
                    //        start: start
                    //    });
                    //    var zoom = ol.animation.zoom({
                    //        duration: 1000,
                    //        resolution: map.getView().getResolution()
                    //    });
                    //
                    //    map.beforeRender(zoom, pan);
                    //}

                    /**
                     * Move and zoom the map to a
                     * certain location via a latitude
                     * and longitude
                     * @function zoomTo
                     * @memberof Map
                     * @param {number} lat - Latitude
                     * @param {number} lon - Longitude
                     */
                    //function zoomTo(lat, lon) {
                    //
                    //    zoomAnimate();
                    //    map.getView().setCenter(ol.proj.transform([parseFloat(lon), parseFloat(lat)], 'EPSG:4326', 'EPSG:3857'));
                    //    map.getView().setZoom(zoomToLevel);
                    //    addMarker(parseFloat(lat),parseFloat(lon), searchLayerVector);
                    //
                    //}

                    /**
                     * Move and zoom the map to a
                     * certain location via a latitude
                     * and longitude
                     * @function zoomToExt
                     * @memberof Map
                     * @param {obj} inputExtent - inputExtent
                     */
                    //function zoomToExt(inputExtent) {
                    //
                    //    clearLayerSource(searchLayerVector);
                    //
                    //    var neFeature = new ol.Feature({
                    //        geometry: new ol.geom.Point(ol.proj.transform([inputExtent.bounds.ne.lng, inputExtent.bounds.ne.lat], 'EPSG:4326', 'EPSG:3857'))
                    //    });
                    //
                    //    var swFeature = new ol.Feature({
                    //        geometry: new ol.geom.Point(ol.proj.transform([inputExtent.bounds.sw.lng, inputExtent.bounds.sw.lat], 'EPSG:4326', 'EPSG:3857'))
                    //    });
                    //
                    //    searchLayerVector.getSource().addFeatures([neFeature, swFeature]);
                    //
                    //    var searchItemExtent = searchLayerVector.getSource().getExtent();
                    //
                    //    zoomAnimate();
                    //
                    //    // Moves the map to the extent of the search item
                    //    map.getView().fit(searchItemExtent, map.getSize());
                    //
                    //    // Clean up the searchLayer extent for the next query
                    //    searchLayerVector.getSource().clear();
                    //
                    //    // Add the WKT to the map to illustrate the boundary of the search item
                    //    if (inputExtent.wkt !== undefined){
                    //
                    //        wktFormat = new ol.format.WKT();
                    //        // WKT string is in 4326 so we need to reproject it for the current map
                    //        searchFeatureWkt = wktFormat.readFeature(inputExtent.wkt, {
                    //            dataProjection: 'EPSG:4326',
                    //            featureProjection: 'EPSG:3857'
                    //        });
                    //
                    //        searchFeatureWkt.setStyle(wktStyle);
                    //        searchLayerVector.getSource().addFeatures([searchFeatureWkt]);
                    //
                    //    }
                    //    else {
                    //        // Add a marker to the map if there isn't a wkt
                    //        // present with the search item
                    //        zoomTo(inputExtent.lat, inputExtent.lng);
                    //    }
                    //
                    //
                    //}

                    //mapView = new ol.View({
                    //    center: [scope.params.lng, scope.params.lat],
                    //    //center: [0, 0],
                    //    projection: 'EPSG:4326',
                    //    zoom: 16
                    //});
                    //map = new ol.Map({
                    //    layers: [
                    //        new ol.layer.Tile({
                    //            source: new ol.source.TileWMS({
                    //                url: 'http://geoserver-demo01.dev.ossim.org/geoserver/ged/wms?',
                    //                params: {'LAYERS': 'osm-group', 'TILED': true},
                    //                serverType: 'geoserver'
                    //            })
                    //        })
                    //    ],
                    //    controls: ol.control.defaults({
                    //        attributionOptions: ({
                    //            collapsible: false
                    //        })
                    //    }),
                    //    target: attrs.id,
                    //    view: mapView
                    //});

                    //map.addLayer(searchLayerVector);

                },
                template: '<div></div>',
                replace: true

            };
        }

})();