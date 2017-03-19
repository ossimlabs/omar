(function() {
    'use strict';
    angular
        .module( 'omarApp' )
        .controller( 'MapOrthoController', ['$scope', '$state', '$stateParams', '$http', '$location', 'downloadService', 'shareService', 'stateService', 'beNumberService', MapOrthoController]);

    function MapOrthoController( $scope, $state, $stateParams, $http, $location, downloadService, shareService, stateService, beNumberService ) {

        // #################################################################################
        // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
        // provides access to various client params in application.yml
        // #################################################################################
        //console.log('AppO2.APP_CONFIG in MapOrthoController: ', AppO2.APP_CONFIG);

        /* jshint validthis: true */
        var vm = this;

        var mapOrtho,
            mapOrthoView,
            imageLayers,
            imageLayerIds,
            footprintLayer,
            placemarkLayer,
            recommendImageId;

        var wfsRequest = {
            typeName: 'omar:raster_entry',
            namespace: 'http://omar.ossim.org',
            version: '1.1.0',
            outputFormat: 'JSON',
            cql: ''
        };

        imageLayerIds = $stateParams.layers.split(",");

        vm.loading = true;

        vm.baseServerUrl = AppO2.APP_CONFIG.serverURL;

        vm.shareModal = function( imageLink ) {
          console.log('imageLink: ',imageLink);
          shareService.imageLinkModal( imageLink );
        };

        vm.archiveDownload = function( imageId ) {
          downloadService.downloadFiles( imageId );
        };

        vm.imageLayerIds = imageLayerIds;
        //console.log('imageLayerIds', imageLayerIds);

        footprintLayer = new ol.layer.Vector({
            visible: false,
            title: 'Image Footprints',
            opacity: 1.0,
            source: new ol.source.Vector({ wrapX: false }),
            style: new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(0, 0, 0, 0)'
                }),
                stroke: new ol.style.Stroke({
                    width: 3.0,
                    color: 'rgba(192, 192, 192, 0.6)'
                })
            })
        });

        placemarkLayer = new ol.layer.Vector({
            title: 'Placemarks',
            source: new ol.source.Vector({ wrapX: false })

        });

        $scope.$on('placemarks: updated', function(event, data) {
            setupPlacemarkLayer(data);
        });

        var viewParams = $location.search();

        if (viewParams.lat && viewParams.lon && viewParams.resolution) {
            viewParams.lat = parseFloat(viewParams.lat);
            viewParams.lon = parseFloat(viewParams.lon);
            viewParams.resolution = parseFloat(viewParams.resolution);

            // console.log( 'viewParams', viewParams );

            getImageBounds(imageLayerIds, true);

            mapOrthoView = new ol.View({
                center: [viewParams.lon, viewParams.lat],
                projection: 'EPSG:4326',
                resolution: viewParams.resolution
            });
        } else {
            getImageBounds(imageLayerIds);

            mapOrthoView = new ol.View({
                center: [0, 0],
                projection: 'EPSG:4326',
                zoom: 2,
                minZoom: 3,
                maxZoom: 18
            });
        }


        function getImageBounds(imageIds, flag) {
            wfsRequest.cql = 'id in(' + imageIds + ')';

            //console.log('wfsRequest.cql', wfsRequest.cql);

            //var wfsRequestUrl = '/o2/wfs?';
            var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;

            var wfsUrl = wfsRequestUrl +
                "service=WFS" +
                "&version=" + wfsRequest.version +
                "&request=GetFeature" +
                "&typeName=" + wfsRequest.typeName +
                "&filter=" + encodeURIComponent( wfsRequest.cql ) +
                "&outputFormat=" + wfsRequest.outputFormat;
            var url = wfsUrl;

            $http({
                method: 'GET',
                url: url
            }).then(function(response) {

                var data;
                data = response.data.features;

                var geoms = [];

                //console.log('mapOrtho WFS data: ', data);
                //console.log('data.length', data.length);

                // If there is more than one image we can get the extent
                // of the footprintLayer to set the maps extent
                if (data.length > 1) {
                    // Add each image to the footprintLayer
                    angular.forEach(data, function(image) {
                        var geom = new ol.geom.MultiPolygon(image.geometry.coordinates);

                        geoms.push(geom);

                        var imageFeature = new ol.Feature({
                            geometry: geom
                        });

                        footprintLayer.getSource().addFeature(imageFeature);

                    });


                    if (!flag) {
                        var footprintLayerExtent = footprintLayer.getSource().getExtent();

                        // Sets the map's extent to all of the images in the footprintLayer
                        mapOrtho.getView().fit(footprintLayerExtent, mapOrtho.getSize());

                    }
                }
                // If there is only one image we need to use the extent of the feature (image)
                // in the footprintLayer
                else {
                    var geom = new ol.geom.MultiPolygon(data[0].geometry.coordinates);

                    var imageFeature = new ol.Feature({
                        geometry: geom
                    });

                    geoms.push(geom);

                    if (!flag) {
                        var featureExtent = imageFeature.getGeometry().getExtent();


                        // Moves the map to the extent of the one image
                        mapOrtho.getView().fit(featureExtent, mapOrtho.getSize());
                    }
                }

                var totalGeoms = new ol.geom.GeometryCollection(geoms);

                if (AppO2.APP_CONFIG.params.misc.placemarks) {
                    beNumberService.getBeData(totalGeoms);
                }

                // set header title
                var headerTitle = [];
                angular.forEach(data, function(image) {
                    var imageIdText = image.properties.title || image.properties.filename;
                    var acquisitionDateText = image.properties.acquisition_date || "";
                    if (acquisitionDateText != "") {
                      acquisitionDateText = moment.utc(acquisitionDateText).format('MM-DD-YYYY HH:mm:ss') + " z";
                    }
                    var text = imageIdText + (acquisitionDateText ? " : " + acquisitionDateText : "");
                    headerTitle.push(text);
                });
                stateService.navStateUpdate({ titleLeft: headerTitle.join(", ") });
            });
        }

        imageLayers = new ol.layer.Tile({
            title: 'Images',
            opacity: 1.0,
            source: new ol.source.TileWMS({
                //url: '/o2/wms?',
                url: AppO2.APP_CONFIG.params.wms.baseUrl,
                params: {
                    'LAYERS': 'omar:raster_entry',
                    'FILTER': "in(" + imageLayerIds + ")",
                    'TILED': true,
                    'VERSION': '1.1.1'
                },
		wrapX: false
            }),
            name: imageLayerIds
        });

        var mousePositionControl = new ol.control.MousePosition( {
            coordinateFormat: function ( coord ) {
                var html = "";
                var point = new GeoPoint( coord[0], coord[1] );
                switch(mousePositionControl.coordFormat) {
                    // dd
                    case 0: html = coord[1].toFixed( 6 ) + ', ' + coord[0].toFixed( 6 ); break;
                    // dms w/cardinal direction
                    case 1: html = point.getLatDegCard() + ', ' + point.getLonDegCard(); break;
                    // dms w/o cardinal direction
                    case 2: html = point.getLatDeg() + ', ' + point.getLonDeg(); break;
                    // mgrs
                    case 3: html = mgrs.forward( coord, 5 ); break;
                }
                document.getElementById( 'mouseCoords').innerHTML = html;
            },
            projection: 'EPSG:4326',
            // comment the following two lines to have the mouse position
            // be placed within the map.
            className: 'custom-mouse-position',
            //target: document.getElementById('mouse-position'),
            undefinedHTML: '&nbsp;'
        } );
	mousePositionControl.coordFormat = 0;
        $('#mouseCoords').click(function() {
            mousePositionControl.coordFormat = mousePositionControl.coordFormat >= 3 ? 0 : mousePositionControl.coordFormat + 1;
        });

        var interactions = ol.interaction.defaults({
            altShiftDragRotate: true
        });

        var baseMapGroup = new ol.layer.Group({
            'title': 'Base maps',
            layers: []
        });

        // Takes a map layer obj, and adds
        // the layer to the map layers array.
        function addBaseMapLayers(layerObj) {

            var baseMapLayer;
            if (layerObj.layerType.toLowerCase() === 'tile') {

                var baseMapLayer = new ol.layer.Tile({
                    title: layerObj.title,
                    type: 'base',
                    visible: layerObj.options.visible,
                    source: new ol.source.TileWMS({
                        url: layerObj.url,
                        params: {
                            'VERSION': '1.1.1',
                            'LAYERS': layerObj.params.layers,
                            'FORMAT': layerObj.params.format
                        },
			wrapX: false
                    }),
                    name: layerObj.title
                });

            }

            if (baseMapLayer != null) {

                // Add layer(s) to the layerSwitcher control
                baseMapGroup.getLayers().push(baseMapLayer);

            }

        }

        // Map over each layer item in the layerList array
        //AppO2.APP_CONFIG.params.baseMaps.layerList.map(addBaseMapLayers);
        AppO2.APP_CONFIG.openlayers.baseMaps.map(addBaseMapLayers);

        // create full screen control
        var span = document.createElement('span');
        span.className = 'glyphicon glyphicon-fullscreen';
        var fullScreenControl = new ol.control.FullScreen({
            label: span
        });

        mapOrtho = new ol.Map({
            layers: [
                baseMapGroup,
                new ol.layer.Group({
                    title: 'Overlays',
                    layers: [
                        imageLayers,
                        footprintLayer,
                        placemarkLayer
                    ]
                })
            ],
            controls: ol.control.defaults().extend([
                fullScreenControl,
                new ol.control.ScaleLine()
            ]).extend([mousePositionControl]),
            interactions: interactions,
            logo: false,
            target: document.getElementById('mapOrtho'),
            view: mapOrthoView
        });

        setupContextDialog();

        setupPopupLayer();

        function setupContextDialog() {
            mapOrtho.getViewport().addEventListener("contextmenu",
                function (event) {
                    event.preventDefault();
                    var pixel = [event.layerX, event.layerY];
                    var coord = mapOrtho.getCoordinateFromPixel(pixel);
                    if (coord) {
                        var point = new GeoPoint(coord[0], coord[1]);
                        var ddPoint = point.getLatDec().toFixed(6) + ', ' + point.getLonDec().toFixed(6);
                        var dmsPoint = point.getLatDegCard() + ' ' + point.getLonDegCard();
                        var mgrsPoint = mgrs.forward(coord, 5);
                        $('#contextMenuDialog .modal-body').html(ddPoint + " // " + dmsPoint + " // " + mgrsPoint);
                        $('#contextMenuDialog').modal('show');
                    }
                }
            );
        }

        function setupPopupLayer()
        {
            var element = document.getElementById('popup');

            var popup = new ol.Overlay({
                element: element,
                positioning: 'bottom-center',
                stopEvent: false,
                offset: [0, -50]
            });
            mapOrtho.addOverlay(popup);

            // display popup on click
            mapOrtho.on('click', function(evt) {

                var feature = mapOrtho.forEachFeatureAtPixel(evt.pixel, function(feature) {
                    return feature;
                });

                if (feature) {
                    var coordinates = feature.getGeometry().getCoordinates();

                    popup.setPosition(coordinates);

                    var name = feature.get(AppO2.APP_CONFIG.params.misc.placemarks.columnName);

                    $(element).attr('data-placement', 'top');
                    // $( element ).attr( 'data-original-title', feature.get( 'title' ) );
                    $(element).attr('data-content', name);
                    $(element).attr('data-html', true);
                    //now call bootstrap popover function
                    $(element).popover('show');
                } else {
                    $(element).popover('destroy');
                }
            });

            // change mouse cursor when over marker
            mapOrtho.on('pointermove', function(e) {
                if (e.dragging) {
                    $(element).popover('destroy');
                    return;
                }
                var pixel = mapOrtho.getEventPixel(e.originalEvent);
                var hit = mapOrtho.hasFeatureAtPixel(pixel);

                if (mapOrtho.getTarget().style) {
                    mapOrtho.getTarget().style.cursor = hit ? 'pointer' : '';
                }

            });
        }

        function setupPlacemarkLayer(placemarks) {
            // console.log( 'placemarks:', placemarks );

            var iconStyle = new ol.style.Style({
                image: new ol.style.Icon( /** @type {olx.style.IconOptions} */ ({
                    anchor: [0.5, 46],
                    anchorXUnits: 'fraction',
                    anchorYUnits: 'pixels',
                    src: vm.baseServerUrl + '/' + AppO2.APP_CONFIG.params.misc.icons.greenMarker
                }))
            });

            placemarks.map(function(placemark) {
                var data = placemark.properties;

                data.geometry = new ol.geom.Point(placemark.geometry.coordinates);

                var iconFeature = new ol.Feature(data);

                iconFeature.setStyle(iconStyle);
                placemarkLayer.getSource().addFeature(iconFeature);
            });
        }

        var layerSwitcher = new ol.control.LayerSwitcher({
            tipLabel: 'Layers' // Optional label for button
        });
        mapOrtho.addControl(layerSwitcher);

    }

}());
