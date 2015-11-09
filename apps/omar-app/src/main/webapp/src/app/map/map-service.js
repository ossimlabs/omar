'use strict';
angular
    .module('omarApp')
    .service('mapService', mapService);

function mapService ($q) {
    var zoomToLevel = 14;
    var map,
        mapView,
        searchLayerVector // Used for visualizing the search items map markers polygon boundaries

    this.mapServiceTest = function(){
        console.log('mapServiceTest firing!');
    };

    this.mapInit = function(target, lng, lat){
        mapView = new ol.View({
            center: [lng, lat],
            projection: 'EPSG:4326',
            zoom: 4
        });
        map = new ol.Map({
            layers: [
                new ol.layer.Tile({
                    source: new ol.source.TileWMS({
                        url: 'http://geoserver-demo01.dev.ossim.org/geoserver/ged/wms?',
                        params: {'LAYERS': 'osm-group', 'TILED': true},
                        serverType: 'geoserver'
                    })
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
        //mapZoomTo(lat, lng);
    };

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


