(function () {
    'use strict';
    angular
        .module('omarApp')
        .service('mapService', ['APP_CONFIG', 'wfsService', mapService]);

    function mapService(APP_CONFIG, wfsService) {

        // Add the basemap parameters from the applicaiton config file.
        var osmBaseMapUrl = APP_CONFIG.services.basemaps.osm.url;
        var osmBaseMapLayers = APP_CONFIG.services.basemaps.osm.layers;

        // Add the path to OMAR and the footprints URL
        //var omarUrl = APP_CONFIG.services.omar.url;
        //var omarPort = APP_CONFIG.services.omar.port || '80';
        //var omarFootprintsUrl = APP_CONFIG.services.omar.footprintsUrl;

        //console.log('omarFootprintsUrl', omarFootprintsUrl);

        var zoomToLevel = 16;
        var map,
            mapView,
            searchLayerVector, // Used for visualizing the search items map markers polygon boundaries
            geomField,
            wktFormat,
            searchFeatureWkt,
            iconStyle,
            wktStyle,
            footprintStyle;

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

        footprintStyle = new ol.style.Style({
            fill: new ol.style.Fill({
                color: 'rgba(255, 100, 50, 0.6)'
            }),
            stroke: new ol.style.Stroke({
                width: 5.5,
                //color: 'rgba(255, 100, 50, 0.6)'
            })
        });

        searchLayerVector = new ol.layer.Vector({
            source: new ol.source.Vector()
        });

        /**
         * Elements that make up the popup.
         */
        var container = document.getElementById('popup');
        var content = document.getElementById('popup-content');
        var closer = document.getElementById('popup-closer');

        /**
         * Create an overlay to anchor the popup to the map.
         */
        var overlay = new ol.Overlay(/** @type {olx.OverlayOptions} */ ({
            element: container
        }));

        this.mapInit = function (mapParams) {

            //console.log('mapParams', mapParams);

            mapView = new ol.View({
                center: [0, 0],
                projection: 'EPSG:4326',
                zoom: 12,
                minZoom: 3,
                maxZoom: 18
            });

            var baseMap = new ol.layer.Tile({
                source: new ol.source.TileWMS({
                    url: osmBaseMapUrl,
                    params: {'LAYERS': osmBaseMapLayers, 'TILED': true},
                    serverType: 'geoserver'
                }),
                name: 'Open Street Map'
            });

            var footPrints = new ol.layer.Tile({
                source: new ol.source.TileWMS({
                    url: '/o2/footprints/getFootprints',
                    params: {
                        FILTER: "",
                        VERSION: '1.1.1',
                        LAYERS: 'omar:raster_entry',
                        STYLES: 'byFileType'
                    }
                }),
                name: 'Image Footprints'
            });

            map = new ol.Map({
                layers: [
                    baseMap, footPrints
                ],
                controls: ol.control.defaults().extend([
                    //new ol.control.FullScreen(),
                    new ol.control.ScaleLine()
                ]),
                overlays: [overlay],
                target: 'map',
                view: mapView
            });

            this.updateFootPrintLayer = function (filter) {

                //console.log('updating footprint layer with filter:', filter);
                //console.log(footPrints.getSource().getParams());
                var params = footPrints.getSource().getParams();
                params.FILTER = filter;
                console.log('params.FILTER', params.FILTER);
                footPrints.getSource().updateParams(params);


            };

            map.addLayer(searchLayerVector);


            geomField = 'ground_geom';
            var mapObj = {};

            map.on('moveend', function () {

                mapObj.cql = "INTERSECTS(" + geomField + "," + convertToWktPolygon() + ")";

                // Update the image cards in the list via spatial bounds
                wfsService.updateSpatialFilter(mapObj.cql);

            });

            if (mapParams === undefined) {

                //zoomTo(0,0,4);
                zoomTo(33.3116664, 44.2858655, 4)

            }
            else if (mapParams !== undefined && mapParams.bounds === undefined) {

                zoomTo(mapParams.lat, mapParams.lng, zoomToLevel, true);

            }
            else {

                zoomToExt(mapParams);

            }

        };

        this.mapShowImageFootprint = function(imageObj) {

            clearLayerSource(searchLayerVector);

            console.log('mapShowImageFootprint firing: ',imageObj);
            //console.log(geomObj.geometry.coordinates);

            var footprintFeature = new ol.Feature({
                geometry: new ol.geom.MultiPolygon(imageObj.geometry.coordinates)
            });

            var color = setFootprintColors(imageObj.properties.file_type);
            //console.log(color);

            footprintStyle.getFill().setColor(color);
            footprintStyle.getStroke().setColor(color);

            footprintFeature.setStyle(footprintStyle);

            searchLayerVector.getSource().addFeature(footprintFeature);

            var featureExtent = footprintFeature.getGeometry().getExtent();
            var featureExtentCenter = new ol.extent.getCenter(featureExtent);

            var missionID = "Unknown";
            var sensorID = "Unknown";
            var acquisition_date = "Unknown"

            if (imageObj.properties.mission_id != undefined) {
                missionID = imageObj.properties.mission_id;
            }
            if (imageObj.properties.sensor_id != undefined) {
                sensorID = imageObj.properties.sensor_id;
            }
            if (imageObj.properties.acquisition_date != undefined) {
                acquisition_date = moment(imageObj.properties.acquisition_date).format('MM/DD/YYYY HH:mm:ss');
            }

            content.innerHTML =
            '<div class="media">' +
                '<div class="media-left">' +
                    '<img class="media-object" ' +
                        'src="/o2/imageSpace/getThumbnail?filename=' +
                        imageObj.properties.filename +
                        '&entry=' + imageObj.properties.entry_id +
                        '&size=50' + '&format=jpeg">' +
                '</div>' +
                '<div class="media-body">' +
                    '<small><span class="text-primary">Mission:&nbsp; </span><span class="text-success">' + missionID + '</span></small><br>' +
                    '<small><span class="text-primary">Sensor:&nbsp; </span><span class="text-success">' + sensorID + '</span></small><br>' +
                    '<small><span class="text-primary">Acquisition:&nbsp; </span><span class="text-success">' + acquisition_date + '</span></small>' +
                '</div>' +
            '</div>'

            overlay.setPosition(featureExtentCenter);

        };


        this.mapRemoveImageFootprint = function() {

            clearLayerSource(searchLayerVector);
            //overlay.setPosition(undefined);

        }

        //this.resizeElement = function (element, height){
        //    //console.log('resizing');
        //    $(element).animate({height:$(window).height()- height}, 10, function(){
        //        map.updateSize();
        //    });
        //
        //};

        function getMapBbox() {

            return map.getView().calculateExtent(map.getSize());

        };

        function convertToWktPolygon() {

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
                addMarker(parseFloat(lat), parseFloat(lon), searchLayerVector);
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
            if (inputExtent.wkt !== undefined) {

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

        function zoomAnimate() {

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
        function clearLayerSource(layer) {

            if (layer.getSource().getFeatures().length >= 1) {
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
        function addMarker(lat, lon, layer) {

            clearLayerSource(layer);
            var centerFeature = new ol.Feature({
                geometry: new ol.geom.Point([parseFloat(lon), parseFloat(lat)])
            });
            centerFeature.setStyle(iconStyle);
            layer.getSource().addFeatures([centerFeature]);
        }

        function setFootprintColors(imageType) {

            var color = "rgba(255, 255, 50, 0.6)";

            switch(imageType){
                case "adrg":
                    color = "rgba(50, 111, 111, 0.6)"; // atoll
                    break;
                case "aaigrid":
                    color = "rgba(255, 192, 203, 0.6)"; // pink
                    break;
                case "cadrg":
                    color = "rgba(0, 255, 255, 0.6)"; // cyan
                    break;
                case "ccf":
                    color = "rgba(128, 100, 255, 0.6)"; // light slate blue
                    break;
                case "cib":
                    color = "rgba(0, 128, 128, 0.6)"; // teal
                    break;
                case "doqq":
                    color = "rgba(128, 0, 128, 0.6)"; // purple
                    break;
                case "dted":
                    color = "rgba(0, 255, 0, 0.6)"; // green
                    break;
                case "imagine_hfa":
                    color = "rgba(211, 211, 211, 0.6)"; // lightGrey
                    break;
                case "jpeg":
                    color = "rgba(255, 255, 0, 0.6)"; // yellow
                    break;
                case "jpeg2000":
                    color = "rgba(255, 200, 0, 0.6)"; // orange
                    break;
                case "landsat7":
                    color = "rgba(255, 0, 255, 0.6)"; // pink
                    break;
                case "mrsid":
                    color = "rgba(0, 188, 0, 0.6)"; // light green
                    break;
                case "nitf":
                    color = "rgba(0, 0, 255, 0.6)"; // blue
                    break;
                case "tiff":
                    color = "rgba(255, 0, 0, 0.6)"; // red
                    break;
                case "mpeg":
                    color = "rgba(164, 254, 255, 0.6)"; // red
                    break;
                case "unspecified":
                    color = "rgba(255, 255, 255, 0.6)"; // white
                    break;
                default:
                    color = "rgba(255, 255, 255, 0.6)"; // white

            }

            return color;


        }

    }

}());

