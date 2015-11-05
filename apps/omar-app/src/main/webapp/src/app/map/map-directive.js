'use strict';
angular
    .module('omarApp')
    .directive('map', map);

    function map() {
        return {
            restrict: 'A',
            scope: {
                jedi: '='
            },
            link: function(scope, element, attrs){

                console.log('scope.jedi', scope.jedi.value);
                console.log('jedi.lat', scope.jedi.lat);
                console.log('jedi.lon', scope.jedi.lng);

                //console.log('scope', scope);
                //console.log('element', element);
                //console.log('attrs.id', attrs.id);

                var map = new ol.Map({
                    target: attrs.id,
                    layers: [
                        //new ol.layer.Tile({
                        //    source: new ol.source.OSM()
                        //}),
                        new ol.layer.Tile({
                            source: new ol.source.TileWMS({
                                url: 'http://geoserver-demo01.dev.ossim.org/geoserver/ged/wms?',
                                params: {'LAYERS': 'osm-group', 'TILED': true},
                                serverType: 'geoserver'
                            })
                        }),
                        //new ol.layer.Tile({
                        //    //extent: [-13884991, 2870341, -7455066, 6338219],
                        //    source: new ol.source.TileWMS({
                        //        url: 'http://demo.boundlessgeo.com/geoserver/wms',
                        //        params: {'LAYERS': 'topp:states', 'TILED': true},
                        //        serverType: 'geoserver'
                        //    })
                        //}),
                        //new ol.layer.Tile({
                        //    source: new ol.source.TileWMS({
                        //        url: 'http://demo.boundlessgeo.com/geoserver/wms',
                        //        params: {
                        //            'LAYERS': 'ne:NE1_HR_LC_SR_W_DR'
                        //        }
                        //    })
                        //}),
                        //new ol.layer.Tile({
                        //    source: new ol.source.TileWMS({
                        //        url: 'http://10.0.10.186:8888/omar/wms/footprints?',
                        //        params: {
                        //            VERSION: '1.1.1',
                        //            LAYERS: 'Imagery',
                        //            SRS: 'EPSG:4326',
                        //            FORMAT: 'image/png',
                        //            STYLES: "byFileType",
                        //            TRANSPARENT: true,
                        //            TILED: true
                        //        },
                        //    })
                        //})
                        new ol.layer.Tile( {
                            source: new ol.source.TileWMS( {
                                url: 'http://localhost:8888/omar/wms/footprints?',
                                params: {
                                    VERSION: '1.1.1',
                                    SRS: 'EPSG:3857',
                                    LAYERS: 'Imagery',
                                    FORMAT: 'image/png',
                                    STYLES: 'byFileType'
                                }
                            } )
                        } )
                    ],
                    view: new ol.View({
                        //center: ol.proj.fromLonLat([-80.7253178,28.1174627]),
                        projection: 'EPSG:4326',
                        //center: [-80.7253178, 28.1174627],
                        center: [scope.jedi.lng, scope.jedi.lat],
                        zoom: 16
                    })
                });

            },
            template: '<div></div>',
            replace: true

        };
    }
