'use strict';
angular
    .module('omarApp')
    .service('mapService', mapService);

function mapService (APP_CONFIG, $q) {

    // Add the basemap parameters from the applicaiton config
    // file.
    var osmBaseMapUrl = APP_CONFIG.services.basemaps.osm.url;
    var osmBaseMapLayers = APP_CONFIG.services.basemaps.osm.layers;
    // Add the path to OMAR and the footprints URL
    var omarUrl = APP_CONFIG.services.omar.url;
    var omarPort = APP_CONFIG.services.omar.port;
    var omarFootprintsUrl = APP_CONFIG.services.omar.footprintsUrl;
    console.log('footprints', omarUrl + omarPort + omarFootprintsUrl);

    var zoomToLevel = 14;
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
            src: 'assets/search_marker_green.png'
        }))
    });

    this.mapServiceTest = function(){
        console.log('mapServiceTest firing!');
    };

    this.mapInit = function(target, lng, lat){
        mapView = new ol.View({
            //center: [lng, lat],
            center: ol.proj.fromLonLat([-80.7253178, 28.1174627]),
            //projection: 'EPSG:4326',
            zoom: 14
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
                        url: omarUrl + omarPort + omarFootprintsUrl,
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
            target: target,
            view: mapView
        });
        mapZoomTo(lat, lng);
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
    function zoomTo(lat, lon) {

        zoomAnimate();
        map.getView().setCenter(ol.proj.transform([parseFloat(lon), parseFloat(lat)], 'EPSG:4326', 'EPSG:3857'));
        map.getView().setZoom(zoomToLevel);
        addMarker(parseFloat(lat),parseFloat(lon), searchLayerVector);

    }

    searchLayerVector = new ol.layer.Vector({
        source: new ol.source.Vector()
    });

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
     * Move and zoom the map to a
     * certain location via a latitude
     * and longitude
     * @function zoomTo
     * @memberof Map
     * @param {number} lat - Latitude
     * @param {number} lon - Longitude
     */
    var mapZoomTo = function (lat, lon) {

        zoomAnimate();
        map.getView().setCenter(ol.proj.transform([parseFloat(lon), parseFloat(lat)], 'EPSG:4326', 'EPSG:3857'));
        map.getView().setZoom(zoomToLevel);
        addMarker(parseFloat(lat),parseFloat(lon), searchLayerVector);

    };

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
            geometry: new ol.geom.Point(ol.proj.transform([parseFloat(lon), parseFloat(lat)], 'EPSG:4326', 'EPSG:3857'))
        });
        centerFeature.setStyle(iconStyle);
        layer.getSource().addFeatures([centerFeature]);
    }

}


