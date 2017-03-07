//= require jquery-2.2.0.min.js
//= require webjars/openlayers/3.17.1/ol.js
//= require ol3-layerswitcher.js
//= require geopoint.js
//= require mgrs.js

//= require_self

var MapView = (function() {
  function init(params) {

    // ########################
    //console.log(params);
    // ########################

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

    var dgi = new ol.layer.Tile({
        title: 'DGI Imagery',
        source: new ol.source.TileWMS({
            url: (params.contextPath || '') + '/evwhs/wms',
            params: {}
        })
    });
    dgi.setOpacity(0.75);
    overlayGroup.getLayers().push(dgi);

    // TODO: This needs to come from the app.yml
    var wkt = "POLYGON ((24.5702488 11.5219801, 33.0690212 4.2862353, 30.075652 -0.0313078, 20.6879993 6.8059724, 21.4633531 11.0584089, 24.5702488 11.5219801))"

    var format = new ol.format.WKT();

    var feature = format.readFeature(wkt);

    var vector = new ol.layer.Vector({
      title: 'Project Area',
      source: new ol.source.Vector({
        features: [feature]
      })
    });

    wktStyle = new ol.style.Style({
      stroke: new ol.style.Stroke({
        width: 5.5,
        color: 'rgba(255, 100, 50, 0.8)'
      })
    });
    vector.setStyle(wktStyle);
    overlayGroup.getLayers().push(vector);

    // Map over each map item in the baseMaps array
    params.openlayers.baseMaps.map(addBaseMapLayers);

    var osm = new ol.layer.Tile({
      title: 'OSM Nightly',
      type: 'base',
      visible: false,
      source: new ol.source.OSM()
    });
    baseMapGroup.getLayers().push(osm);

    var layers = [
        baseMapGroup,
        overlayGroup
    ];

    var mousePositionControl = new ol.control.MousePosition({
      coordinateFormat: function (coord) {
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
    });
    mousePositionControl.coordFormat = 0;
    $('#mouseCoords').click(function() {
      mousePositionControl.coordFormat = mousePositionControl.coordFormat >= 3 ? 0 : mousePositionControl.coordFormat + 1;
    });

    var map = new ol.Map({
      controls: ol.control.defaults().extend([new ol.control.ScaleLine(), mousePositionControl]),
      // interactions: ol.interaction.defaults().extend([
      //   new ol.interaction.DragRotateAndZoom()
      // ]),
      layers: layers,
      target: 'map',
      view: new ol.View({
        projection: 'EPSG:4326',
        center: [26.5471866799007, 6.088309418728136],
        extent: [20.6879993,-0.0313078,33.0690212,11.5219801],
        minZoom: 5,
        maxZoom: 22,
        zoom: 5
      }),
      logo: false
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
      tipLabel: 'Layers'
    });
    map.addControl(layerSwitcher);

    var $opacityInput = $('.opacity');
    $opacityInput.on('input change', function(){
      dgi.setOpacity(parseFloat(this.value));
    });
    $opacityInput.val(String(dgi.getOpacity()));

    var $zoomLevelSpan = $('#zoomLevel');
    map.on('moveend', function() {
      $zoomLevelSpan.html(map.getView().getZoom());
    });




















    // Begin Zoom Stuffs
    //####################################################################

    var zoomToLevel = 16; // Change this to desired zoom level

    // Cache DOM elements.  Modify to your form element names.
    var $zoomToForm = $('#zoomToForm');
    var $zoomButton =  $('#zoomButton');
    var $coordInput = $('#coordInput');
    // ********************************************************************

    var lat,
        lon,
        latNum,
        latDir,
        lonNum,
        lonDir;

    // Regular expression for the input types
    var dRegExp = /^\s*(\-?\d{1,2})\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3})\s*\u00B0?\s*([WwEe])?\s*$/;
    var ddRegExp = /^\s*(\-?\d{1,2}\.\d*)\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3}\.\d*)\s*\u00B0?\s*([WwEe])?\s*$/;
    var dmsRegExp = /^\s*(\d{1,2})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([NnSs])\s*(\d{1,3})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([EeWw])\s*$/;
    var mgrsRegExp = /^\s*(\d{1,2})\s*([A-Za-z])\s*([A-Za-z])\s*([A-Za-z])\s*(\d{1,5})\s*(\d{1,5})\s*$/;

    // Bind events
    $zoomButton.on("click", cycleRegExs);
    $zoomToForm.keypress(suppressKey);

    // Suppress <Enter> key from causing a submit behavior
    function suppressKey (event) {
        if (event.keyCode == 10 || event.keyCode == 13){
            event.preventDefault();
        }
    }

    function getNum(val) {
        if (typeof val === 'undefined'){
            return "";
        }
        else if (isNaN(val)){
            return "";
        }
        return val;
    }

    function cycleRegExs() {

        var coordInput = $coordInput.val();
        coordInput.trim();

        if (coordInput.match(ddRegExp)) {

            //console.log(coordInput.match(ddRegExp));
            //console.log('0= ' + coordInput.match(ddRegExp)[0]);
            //console.log('1= ' + coordInput.match(ddRegExp)[1]);
            //console.log('2= ' + coordInput.match(ddRegExp)[2]);
            //console.log('3= ' + coordInput.match(ddRegExp)[3]);

            latNum = coordInput.match(ddRegExp)[1];
            latDir = coordInput.match(ddRegExp)[2];

            lonNum = coordInput.match(ddRegExp)[3];
            lonDir = coordInput.match(ddRegExp)[4];

            if ((latNum >= -90 && latNum <= 90) && (lonNum >= -180 && lonNum <= 180)) {

                // check if lat is north or south
                if(latDir === "S" || latDir === "s") {
                    lat = -latNum;
                }
                else {
                    lat = latNum;
                }

                // check if lon is east or west
                if(lonDir === "W" || lonDir === "w") {
                    lon = -lonNum;
                }
                else {
                    lon = lonNum;
                }

                zoomTo(lat, lon);

            }
            else {
                toastr.error('Sorry, could not locate coordinates: [' + $coordInput.val() + '] Please check the' +
                    ' formatting' +
                    ' and' +
                    ' try' +
                    ' again.', 'No Match');
            }

            console.log('DD Match');
            console.log('input: ' + coordInput);
            console.log('result: ' + lat + " " + lon);
        }

        else if (coordInput.match(dRegExp)) {

            //console.log(coordInput.match(ddRegExp));
            //console.log('0= ' + coordInput.match(ddRegExp)[0]);
            //console.log('1= ' + coordInput.match(ddRegExp)[1]);
            //console.log('2= ' + coordInput.match(ddRegExp)[2]);
            //console.log('3= ' + coordInput.match(ddRegExp)[3]);

            latNum = coordInput.match(dRegExp)[1];
            latDir = coordInput.match(dRegExp)[2];

            lonNum = coordInput.match(dRegExp)[3];
            lonDir = coordInput.match(dRegExp)[4];

            if ((latNum >= -90 && latNum <= 90) && (lonNum >= -180 && lonNum <= 180)) {

                // check if lat is north or south
                if(latDir === "S" || latDir === "s") {
                    lat = -latNum;
                }
                else {
                    lat = latNum;
                }

                // check if lon is east or west
                if(lonDir === "W" || lonDir === "w") {
                    lon = -lonNum;
                }
                else {
                    lon = lonNum;
                }

                zoomTo(lat, lon);
            }
            else {
                toastr.error('Sorry, could not locate coordinates: [' + $coordInput.val() + '] Please check the' +
                    ' formatting' +
                    ' and' +
                    ' try' +
                    ' again.', 'No Match');
            }

            console.log('D Match');
            console.log('input: ' + coordInput);
            console.log('result: ' + lat + " " + lon);
        }

        else if (coordInput.match(dmsRegExp)) {

            //console.log(coordInput.match(dmsRegExp));
            //console.log('0= ' + coordInput.match(dmsRegExp)[0]);
            //console.log('1= ' + coordInput.match(dmsRegExp)[1]);
            //console.log('2= ' + coordInput.match(dmsRegExp)[2]);
            //console.log('3= ' + coordInput.match(dmsRegExp)[3]);
            //console.log('4= ' + coordInput.match(dmsRegExp)[4]);
            //console.log('5= ' + coordInput.match(dmsRegExp)[5]);
            //console.log('6= ' + coordInput.match(dmsRegExp)[6]);
            //console.log('7= ' + coordInput.match(dmsRegExp)[7]);
            //console.log('8= ' + coordInput.match(dmsRegExp)[8]);
            //console.log('9= ' + coordInput.match(dmsRegExp)[9]);
            //console.log('10= ' + coordInput.match(dmsRegExp)[10]);

            //var dms = coordInput.match(dmsRegExp)[0];

            var latDeg = coordInput.match(dmsRegExp)[1]; // degrees
            var latMin = coordInput.match(dmsRegExp)[2]; // minutes
            var latSec = (coordInput.match(dmsRegExp)[3]) + getNum(coordInput.match(dmsRegExp)[4]); // seconds decimal
            // number
            var latHem = coordInput.match(dmsRegExp)[5]; // hemisphere

            var lonDeg = coordInput.match(dmsRegExp)[6]; // degrees
            var lonMin = coordInput.match(dmsRegExp)[7]; // minutes
            var lonSec = (coordInput.match(dmsRegExp)[8]) + getNum(coordInput.match(dmsRegExp)[9]); // seconds
            // decimal number
            var lonHem = coordInput.match(dmsRegExp)[10]; // hemisphere

            if ((latDeg >= -90 && latDeg <= 90) && (lonDeg >= -180 && lonDeg <= 180)) {

                lat = dmsToDd(latDeg, latMin, latSec, latHem);
                lon = dmsToDd(lonDeg, lonMin, lonSec, lonHem);
                zoomTo(lat, lon);

            }
            else {
                toastr.error('Sorry, could not locate coordinates: [' + $coordInput.val() + '] Please check the' +
                    ' formatting' +
                    ' and' +
                    ' try' +
                    ' again.', 'No Match');
            }

            console.log('DMS Match');
            console.log('input: ' + coordInput);
            console.log('result: ' + lat + " " + lon);
        }

        else if (coordInput.match(mgrsRegExp)) {

            //var mgrsAll = coordInput.match(mgrsRegExp);
            //var mgrs0 = coordInput.match(mgrsRegExp)[0];
            var mgrs1 = coordInput.match(mgrsRegExp)[1];
            var mgrs2 = coordInput.match(mgrsRegExp)[2];
            var mgrs3 = coordInput.match(mgrsRegExp)[3];
            var mgrs4 = coordInput.match(mgrsRegExp)[4];
            var mgrs5 = coordInput.match(mgrsRegExp)[5];
            var mgrs6 = coordInput.match(mgrsRegExp)[6];

            //console.log('mgrsAll: ' + mgrsAll);
            //console.log('mgrs0: ' + mgrs0);
            //console.log('mgrs1: ' + mgrs1);
            //console.log('mgrs2: ' + mgrs2);
            //console.log('mgrs3: ' + mgrs3);
            //console.log('mgrs4: ' + mgrs4);
            //console.log('mgrs5: ' + mgrs5);
            //console.log('mgrs6: ' + mgrs6);

            // Using mgrs.js toPoint, and then using the zoomTo (set at zoom level 12):
            var mgrsPoint = mgrs.toPoint(mgrs1+mgrs2+mgrs3+mgrs4+mgrs5+mgrs6);
            console.log('------------<mgrsPoint>-----------');
            console.log(mgrsPoint);
            console.log('------------</mgrsPoint>----------');
            zoomTo(mgrsPoint[1], mgrsPoint[0]);

            // ####################################    WIP   #####################################################
            // mgrs.inverse uses the mgrs.js library to return a bounding box.  I am leaving this code here in
            // case we want to have the input mgrs location zoom to the appropriate location on the mgris grid.
            // At this time, if a user that inputs: 33UXP0500444998 it would create a 1m bounding box, and zoom
            // the map to the extent of the bounding box.  We would need to offset the extent by a given factor
            // so that it would not require the user to zoom bout 4-6 times to get to an acceptable level.
            //var bBox = mgrs.inverse(mgrs1+mgrs2+mgrs3+mgrs4+mgrs5+mgrs6);
            //var bBox = mgrs.inverse($coordInput.val());
            //console.log('------------<bBox>-----------');
            //console.log(bBox);
            //console.log('------------</bBox>----------');
            //
            //var mgrsExtent = bBox //[minlon, minlat, maxlon, maxlat];
            //mgrsExtent = ol.extent.applyTransform(mgrsExtent, ol.proj.getTransform("EPSG:4326", "EPSG:3857"));

            //map.getView().fitExtent(mgrsExtent, map.getSize());

            // ####################################    /WIP   ####################################################


        }

        else {
            console.log('No Match');
            toastr.error('Sorry, could not locate coordinates: [' + $coordInput.val() + '] Please check the' +
                ' formatting' +
                ' and' +
                ' try' +
                ' again.', 'No Match');
       }
    }

    function dmsToDd (degrees, minutes, seconds, position) {

        var dd = Math.abs(degrees) + Math.abs(minutes / 60) + Math.abs(seconds / 3600);

        if (position == "S" || position == "s" || position == "W" || position == "w") {
            dd = -dd;
        }

        return dd;
    }

    function zoomTo(lat, lon) {

        var start = + new Date();
        var pan = ol.animation.pan({
            duration: 750,
            source: (map.getView().getCenter()),
            start: start
        });
        var zoom = ol.animation.zoom({
            duration: 1000,
            resolution: map.getView().getResolution()
        });

        map.beforeRender(zoom,pan);
        //map.getView().setCenter(ol.proj.transform([parseFloat(lon), parseFloat(lat)], 'EPSG:4326', 'EPSG:3857'));
        map.getView().setCenter([parseFloat(lon), parseFloat(lat)]);
        map.getView().setZoom(zoomToLevel);

    }

    // function initialize (initParams) {

    // }

    // Parameters for the toastr banner
    // toastr.options = {
    //     "closeButton": true,
    //     "progressBar": true,
    //     "positionClass": "toast-bottom-right",
    //     "showMethod": "fadeIn",
    //     "hideMethod": "fadeOut",
    //     "timeOut": "10000"
    // };

    // return {

    //     initialize: initialize

    // };

  } // end init

  return {
    init: init,
  };

})();
