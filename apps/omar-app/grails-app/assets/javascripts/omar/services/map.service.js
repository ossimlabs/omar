(function() {
'use strict';
angular
  .module('omarApp')
  .service('mapService', ['stateService', 'wfsService', mapService]);

function mapService(stateService, wfsService) {

  // #################################################################################
  // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
  // provides access to various client params in application.yml
  // #################################################################################
  //console.log('AppO2.APP_CONFIG in mapService: ', AppO2.APP_CONFIG);

  var zoomToLevel = 16;
  var map,
      mapView,
      footPrints,
      searchLayerVector, // Used for visualizing the search items map markers polygon boundaries
      filterLayerVector, // Used for visualizing the filter markers and polygon AOI's
      geomField,
      wktFormat,
      searchFeatureWkt,
      iconStyle,
      wktStyle,
      filterStyle,
      footprintStyle,
      dragBox,
      pointLatLon;
  var mapObj = {};

  var baseServerUrl = AppO2.APP_CONFIG.serverURL;
  var markerUrl = baseServerUrl + '/' + AppO2.APP_CONFIG.params.misc.icons.greenMarker;

  iconStyle = new ol.style.Style({
    image: new ol.style.Icon(({
        anchor: [0.5, 46],
        anchorXUnits: 'fraction',
        anchorYUnits: 'pixels',
        //opacity: 0.75,
        //src: AppO2.APP_CONFIG.params.misc.icons.greenMarker
        src: markerUrl
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

  filterStyle = new ol.style.Style({
    fill: new ol.style.Fill({
      color: 'rgba(255, 100, 50, 0.2)'
    }),
    stroke: new ol.style.Stroke({
      width: 5.0,
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
    source: new ol.source.Vector({ wrapX: false })
  });

  filterLayerVector = new ol.layer.Vector({
      source: new ol.source.Vector({ wrapX: false })
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
  var overlay = new ol.Overlay( /** @type {olx.OverlayOptions} */ ({
      element: container
  }));

  this.mapInit = function() {

    mapView = new ol.View({
      center: [0, 0],
      extent: [-180, -90, 180, 90],
      projection: 'EPSG:4326',
      zoom: 12,
      minZoom: 1,
      //maxZoom: 18
    });

    var version="1.1.1";
    var layers="omar:raster_entry";
    var styles="byFileType";
    var format="image/gif";
    var name="Image Footprints";

    if(AppO2.APP_CONFIG.params.footprints.params != undefined )
    {
      if( AppO2.APP_CONFIG.params.footprints.params.version != undefined)
      {
        version = AppO2.APP_CONFIG.params.footprints.params.version;
      }
      if( AppO2.APP_CONFIG.params.footprints.params.layers != undefined)
      {
        layers = AppO2.APP_CONFIG.params.footprints.params.layers;
      }
      if( AppO2.APP_CONFIG.params.footprints.params.styles != undefined)
      {
        styles = AppO2.APP_CONFIG.params.footprints.params.styles;
      }
      if( AppO2.APP_CONFIG.params.footprints.params.format != undefined)
      {
        format = AppO2.APP_CONFIG.params.footprints.params.format;
      }
      if( AppO2.APP_CONFIG.params.footprints.params.name != undefined)
      {
        name = AppO2.APP_CONFIG.params.footprints.params.name;
      }

    }
    footPrints = new ol.layer.Tile({
      title: name,
      source: new ol.source.TileWMS({
        url: AppO2.APP_CONFIG.params.footprints.baseUrl,
        params: {
          FILTER: "",
          VERSION: version,
          LAYERS: layers,
          STYLES: styles,
          FORMAT: format
        },
        wrapX: false
      }),
      name: 'Image Footprints'
    });

    var baseMapGroup = new ol.layer.Group({
        'title': 'Base maps',
        layers: []
    });

    // Takes a map layer obj, and adds
    // the layer to the map layers array.
    function addBaseMapLayers(layerObj) {

      var baseMapLayer;

      switch (layerObj.layerType.toLowerCase()){
        case 'tilewms':
          baseMapLayer = new ol.layer.Tile({
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
          break;
        case 'xyz':
          baseMapLayer = new ol.layer.Tile({
            title: layerObj.title,
            type: 'base',
            visible: layerObj.options.visible,
            source: new ol.source.XYZ({
              url: layerObj.url,
	            wrapX: false
            }),
            name: layerObj.title
          });
          break;
      }

      if (baseMapLayer != null) {
        // Add layer(s) to the layerSwitcher control
        baseMapGroup.getLayers().push(baseMapLayer);
      }

    }

    var overlayGroup = new ol.layer.Group({
      title: 'Overlays',
      layers: []
    });

    // Takes a layer obj, and adds
    // the layer to the overlay layers array.
    function addOverlayLayers(layerObj) {

      var overlayMapLayer = new ol.layer.Tile({
        title: layerObj.title,
        visible: layerObj.options.visible,
        source: new ol.source.TileWMS({
          url: layerObj.url,
          params: {
              FILTER: layerObj.params.filter,
              VERSION: layerObj.params.version,
              LAYERS: layerObj.params.layers,
              STYLES: layerObj.params.styles,
              FORMAT: layerObj.params.format,
          }
        })
      });
      overlayGroup.getLayers().push(overlayMapLayer);
    }

    // Map over each map item in the baseMaps array
    AppO2.APP_CONFIG.openlayers.baseMaps.map(addBaseMapLayers);

    // Map over each layer item in the overlayLayers array
    AppO2.APP_CONFIG.openlayers.overlayLayers.map(addOverlayLayers);
    overlayGroup.getLayers().push(footPrints);

    map = new ol.Map({
      layers: [
        baseMapGroup,
        overlayGroup
      ],
      controls: ol.control.defaults().extend([
        new ol.control.ScaleLine()
      ]).extend([mousePositionControl]),
      logo: false,
      overlays: [overlay],
      target: 'map',
      view: mapView
    });

    setupContextDialog();

    function setupContextDialog() {
        map.getViewport().addEventListener("contextmenu",
            function (event) {
                event.preventDefault();
                var pixel = [event.layerX, event.layerY];
                var coord = map.getCoordinateFromPixel(pixel);
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

    var layerSwitcher = new ol.control.LayerSwitcher({
      tipLabel: 'Layers' // Optional label for button
    });
    map.addControl(layerSwitcher);

    map.addLayer(searchLayerVector);
    map.addLayer(filterLayerVector);

    geomField = 'ground_geom';

    this.viewPortFilter(true);

    dragBox = new ol.interaction.DragBox({
      condition: ol.events.condition.altKeyOnly
    });

    dragBox.on('boxend', function() {

      clearLayerSource(filterLayerVector);

      var dragBoxExtent = dragBox.getGeometry().getExtent();

      mapObj.cql = "INTERSECTS(" + geomField + "," + convertToWktPolygon(dragBoxExtent) + ")";

      // Update the image cards in the list via spatial click coordinates
      wfsService.updateSpatialFilter(mapObj.cql);

      // Grabs the current value of the attrObj.filter so that the click
      // will also update if there are any temporal, keyword, or range filters
      wfsService.updateAttrFilter(wfsService.attrObj.filter);

      var searchPolygon = new ol.Feature({
          geometry: new ol.geom.Polygon.fromExtent(dragBoxExtent)
      });

      searchPolygon.setStyle(filterStyle);
      filterLayerVector.getSource().addFeatures([searchPolygon]);

    });

    zoomTo(stateService.mapState, false);

  };

  this.zoomMap = function(params) {

    if (params.feature.wkt !== undefined) {

      zoomToExt(params);

    } else {

      zoomTo(params, true);

    }

  }

  function updateFootPrints(filter) {

    var params = footPrints.getSource().getParams();
    params.FILTER = filter;
    footPrints.getSource().updateParams(params);

  }

  this.updateFootPrintLayer = function(filter) {

    updateFootPrints(filter);

  };

  // This is used to select images by creating a polygon based on the
  // current map extent and sending it to the wfs service to update the
  // card list
  function filterByViewPort() {

    clearLayerSource(filterLayerVector);

    mapObj.cql = "INTERSECTS(" + geomField + "," + convertToWktPolygon(getMapBbox()) + ")";

    // Update the image cards in the list via spatial bounds
    wfsService.updateSpatialFilter(mapObj.cql);
    //this.updateFootPrintLayer(mapObj.cql);
    //updateFootPrints(mapObj.cql);

  }

  this.viewPortFilter = function(status) {

    if (status) {

        map.on('moveend', filterByViewPort);
        filterByViewPort();

    } else {

        // https://groups.google.com/d/msg/ol3-dev/Z4JoCBs-iEY/HSpihl8bcVIJ
        map.un('moveend', filterByViewPort);
        clearLayerSource(filterLayerVector);

        wfsService.updateSpatialFilter('');

    }

  };

  // This is used to select images by getting the point the user clicked in
  // the map and sending the XY (point) to the wfs service to update the card
  // list
  function filterByPoint(event) {

    clearLayerSource(filterLayerVector);

    var coordinate = event.coordinate;

    var clickCoordinates = coordinate[0] + ' ' + coordinate[1];


    pointLatLon = coordinate[1] + ',' + coordinate[0];


    mapObj.cql = "INTERSECTS(" + geomField + ",POINT(" + clickCoordinates + "))";

    // Update the image cards in the list via spatial click coordinates
    wfsService.updateSpatialFilter(mapObj.cql);

    // Grabs the current value of the attrObj.filter so that the click
    // will also update if there are any temporal, keyword, or range filters
    wfsService.updateAttrFilter(wfsService.attrObj.filter);

    addMarker(coordinate[1], coordinate[0], filterLayerVector);

  };

  this.mapPointLatLon = function() {
    this.pointLatLon = pointLatLon;
  };

  this.pointFilter = function(status) {

    if (status) {

      map.on('singleclick', filterByPoint);

    } else {

      // https://groups.google.com/d/msg/ol3-dev/Z4JoCBs-iEY/HSpihl8bcVIJ
      map.un('singleclick', filterByPoint);
      clearLayerSource(searchLayerVector);
      wfsService.updateAttrFilter(wfsService.attrObj.filter);

    }

  };

  this.polygonFilter = function(status) {

    if (status) {

        // Add interaction
        //filterByViewPort();

        map.addInteraction(dragBox);

    } else {

        // Remove interaction
        map.removeInteraction(dragBox);
        clearLayerSource(filterLayerVector);
    }
  };

  this.mapShowImageFootprint = function(imageObj) {

    clearLayerSource(searchLayerVector);

    var footprintFeature = new ol.Feature({
        geometry: new ol.geom.MultiPolygon(imageObj.geometry.coordinates)
    });

    var color = setFootprintColors(imageObj.properties.file_type);

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
      'src="' + AppO2.APP_CONFIG.params.thumbnails.baseUrl + '?filename=' +
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
    overlay.setPosition(undefined);

  };

    this.getCenter = function() {
        return map.getView().getCenter();
    }
    this.calculateExtent = function() {
        return map.getView().calculateExtent(map.getSize());
    }

  function getMapBbox() {

    return map.getView().calculateExtent(map.getSize());

  }

  function convertToWktPolygon(extent) {

    //var extent = getMapBbox();

    var minX = extent[0];
    var minY = extent[1];
    var maxX = extent[2];
    var maxY = extent[3];

    var wkt = "POLYGON((" + minX + " " + minY + ", " + minX + " " + maxY + ", " + maxX + " " + maxY + ", " +
        maxX + " " + minY + ", " + minX + " " + minY + "))";

    return wkt;

  }

  function zoomTo(params, feature) {

    if (!feature) {

      map.getView().setCenter([parseFloat(params.center.lng), parseFloat(params.center.lat)]);
      map.getView().setZoom(stateService.mapState.zoom);

    } else {

      zoomAnimate();
      map.getView().setZoom(12);
      map.getView().setCenter([parseFloat(params.feature.lng), parseFloat(params.feature.lat)]);

      addMarker(parseFloat(params.feature.lat), parseFloat(params.feature.lng), searchLayerVector);

    }

    resetFeatureMapStateObj();

  }

  function resetFeatureMapStateObj() {

    stateService.resetFeatureMapState();

  }

  function zoomToExt(inputExtent) {

    clearLayerSource(searchLayerVector);

    var neFeature = new ol.Feature({
        geometry: new ol.geom.Point([inputExtent.feature.bounds.ne.lng, inputExtent.feature.bounds.ne.lat])
    });

    var swFeature = new ol.Feature({
        geometry: new ol.geom.Point([inputExtent.feature.bounds.sw.lng, inputExtent.feature.bounds.sw.lat])
    });

    searchLayerVector.getSource().addFeatures([neFeature, swFeature]);

    var searchItemExtent = searchLayerVector.getSource().getExtent();

    zoomAnimate();

    // Moves the map to the extent of the search item
    map.getView().fit(searchItemExtent, map.getSize());

    // Clean up the searchLayer extent for the next query
    searchLayerVector.getSource().clear();

    // Add the WKT to the map to illustrate the boundary of the search item
    if (inputExtent.feature.wkt !== undefined) {

      wktFormat = new ol.format.WKT();
      // WKT string is in 4326 so we need to reproject it for the current map
      searchFeatureWkt = wktFormat.readFeature(inputExtent.feature.wkt, {
          dataProjection: 'EPSG:4326',
          featureProjection: 'EPSG:4326'
      });

      searchFeatureWkt.setStyle(wktStyle);
      searchLayerVector.getSource().addFeatures([searchFeatureWkt]);

    }

    resetFeatureMapStateObj();

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

  function clearLayerSource(layer) {

    if (layer.getSource().getFeatures().length >= 1) {

        layer.getSource().clear();

    }

  }

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

    switch (imageType) {
      // case "adrg":
      //     color = "rgba(50, 111, 111, 0.6)"; // atoll
      //     break;
      // case "aaigrid":
      //     color = "rgba(255, 192, 203, 0.6)"; // pink
      //     break;
      // case "cadrg":
      //     color = "rgba(0, 255, 255, 0.6)"; // cyan
      //     break;
      // case "ccf":
      //     color = "rgba(128, 100, 255, 0.6)"; // light slate blue
      //     break;
      // case "cib":
      //     color = "rgba(0, 128, 128, 0.6)"; // teal
      //     break;
      // case "doqq":
      //     color = "rgba(128, 0, 128, 0.6)"; // purple
      //     break;
      // case "dted":
      //     color = "rgba(0, 255, 0, 0.6)"; // green
      //     break;
      // case "imagine_hfa":
      //     color = "rgba(211, 211, 211, 0.6)"; // lightGrey
      //     break;
      // case "jpeg":
      //     color = "rgba(255, 255, 0, 0.6)"; // yellow
      //     break;
      // case "jpeg2000":
      //     color = "rgba(255, 200, 0, 0.6)"; // orange
      //     break;
      // case "landsat7":
      //     color = "rgba(255, 0, 255, 0.6)"; // pink
      //     break;
      // case "mrsid":
      //     color = "rgba(0, 188, 0, 0.6)"; // light green
      //     break;
      // case "nitf":
      //     color = "rgba(0, 0, 255, 0.6)"; // blue
      //     break;
      // case "tiff":
      //     color = "rgba(255, 0, 0, 0.6)"; // red
      //     break;
      // case "mpeg":
      //     color = "rgba(164, 254, 255, 0.6)"; // red
      //     break;
      // case "unspecified":
      //     color = "rgba(255, 255, 255, 0.6)"; // white
      //     break;
      default:
          color = "rgba(255, 255, 255, 0.6)"; // white

    }

    return color;

  }


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

}

}());
