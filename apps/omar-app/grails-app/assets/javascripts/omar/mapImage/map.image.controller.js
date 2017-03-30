(function () {
  'use strict';
  angular
  .module('omarApp')
  .controller('MapImageController', ['$aside', 'beNumberService', 'coordinateConversionService', 'downloadService', 'imageSpaceService', '$location', '$scope',  'shareService', '$state', '$stateParams', 'toastr',  MapImageController]);

  function MapImageController( $aside, beNumberService, coordinateConversionService, downloadService, imageSpaceService, $location, $scope, shareService, $state, $stateParams, toastr ) {

    var vm = this;

    var imageSpaceObj = {};

    //Used by band selection
    var bands, numberOfBands, bandNum,
    redSelect, greenSelect, blueSelect,
    brightness, brightnesSlider,
    contrast, contrastSlider;

    vm.baseServerUrl = AppO2.APP_CONFIG.serverURL;

    var geoJumpInput = $("#geoJumpInput");
    geoJumpInput.keypress(function(event) {
        if(event.keyCode == 13) { // pressing Return or Enter
            vm.geoJump();
        }
    });
    geoJumpInput.autocomplete({
        dataType: "json",
        minChars: 3,
        onSelect: function ( suggestion ) { vm.geoJump(); },
        serviceUrl: AppO2.APP_CONFIG.serverURL + AppO2.APP_CONFIG.params.twofishes.proxy +
            "/?responseIncludes=WKT_GEOMETRY_SIMPLIFIED&autocomplete=true&maxInterpretations=10&autocompleteBias=BALANCED",
        transformResult: function ( response ) {
            return {
                suggestions: $.map(response.interpretations, function (dataItem) {
                    return {
                        bounds: dataItem.feature.geometry.bounds,
                        data: dataItem.feature.displayName,
                        lat: dataItem.feature.geometry.center.lat,
                        lng: dataItem.feature.geometry.center.lng,
                        value: dataItem.feature.displayName,
                        wkt: dataItem.feature.geometry.wktGeometrySimplified
                    };
                })
            }
        },
        type: "GET"
    });
    geoJumpInput.autocomplete("enable");

    vm.geoJump = function() {
      var location = geoJumpInput.val().trim();
      var coords = coordinateConversionService.convert( location );
    }

    $scope.$on('coordService: updated', function( event, response ) {
        if ( response ) {
            var extent = imageSpaceService.getFootprintGeometry().getExtent();
            if ( ol.extent.containsCoordinate( extent, response.coordinate ) ) {
                var points = [ { "lat": response.coordinate[ 1 ], "lon": response.coordinate[ 0 ] } ];
                var pixels = imageSpaceService.groundToImage( points ).then(
                    function( response ) {
                        if ( response ) {
                            var pixel = [ response.x, response.y * -1 ];
                            imageSpaceService.setCenter( pixel );
                        }
                        else {
                            toastr.error( "Sorry, we couldn't translate that coordinate into pixels." );
                        }
                    }
                );
            }
            else { toastr.error( "That point lies outside the image bounds." ); }
        }
        else {
            toastr.error( "Sorry, we couldn't find anything for that location." );
        }
    });
    $scope.$on( 'coordService: be_search_error', function( event, message ) { toastr.error( message, 'Error' ); } );
    $scope.$on( 'coordService: twofishes_error', function( event, message ) { toastr.error( message, 'Error' ); } );

    vm.shareModal = function() {
      var imageLink = imageSpaceService.getImageLink();
      shareService.imageLinkModal( imageLink );
    };

    vm.archiveDownload = function( imageId ) {
      downloadService.downloadFiles( imageId );
    };

    function checkStateParams() {

      // Check to make sure that all of the $stateParams are defined.
      // If there are undefined params return an error.
      for ( var i in $stateParams ) {

        if ( $stateParams[i] === undefined ) {

          toastr.error( 'There was an issue loading the selected image into the map.',
              'A problem has occurred:', {
              positionClass: 'toast-bottom-left',
              closeButton: true,
              timeOut: 10000,
              extendedTimeOut: 5000,
              target: 'body'
            });

          return;

        }

      }

      // We can load the map, because all parameters
      // are present.
      loadMapImage();
      bandSelection();

    }

    checkStateParams();

    vm.imageId = $stateParams.imageId;

    // Start - Band Selections Section

    function bandSelection() {

      imageSpaceService.getImageBands();

      $scope.bandValues = [];
      $scope.bandTypeValues = [
        { 'key': 0, 'value': 'Default' },
        { 'key': 1, 'value': 'Gray' }
      ];

      bands = imageSpaceService.bands.split( ',' );
      numberOfBands = imageSpaceService.numOfBands;

      for ( bandNum = 0; bandNum < numberOfBands; bandNum++ ) {
          $scope.bandValues.push( { 'key': bandNum + 1, 'value': bandNum + 1 } );
        }

        $scope.enableBandType = true;

        if ( numberOfBands <= 2 ) {
          $scope.grayValue = $scope.bandValues[0].value;
          $scope.grayImageItem = $scope.bandValues[0];

          if ( numberOfBands == 2 ) {
            $scope.enableBandType = true;
          } else {
            $scope.enableBandType = false;
          }

        }else {
          $scope.bandTypeValues.push( { 'key': 2, 'value': 'Color' } );
          $( '#rgb-image-space-bands' ).show();
          $( '#gray-image-space-bands' ).hide();
          $scope.grayImageItem = $scope.bandValues[0];
          $scope.redImageItem = $scope.bandValues[0];
          $scope.greenImageItem = $scope.bandValues[1];
          $scope.blueImageItem = $scope.bandValues[2];
          $scope.rgbValues = { red: $scope.bandValues[0].key,
                            green: $scope.bandValues[1].key,
                            blue: $scope.bandValues[2].key };
        }

       if ( bands.length == 1 ) {
         $scope.bandTypeItem = $scope.bandTypeValues[1];
         $scope.grayImageItem = { 'key': bands[0], 'value': bands[0] };
          $( '#rgb-image-space-bands' ).hide();
          $( '#gray-image-space-bands' ).show();
       }else {
         $scope.bandTypeItem = $scope.bandTypeValues[2];
         $scope.redImageItem = { 'key': bands[0], 'value': bands[0] };
         $scope.greenImageItem = { 'key': bands[1], 'value': bands[1] };
         $scope.blueImageItem = { 'key': bands[2], 'value': bands[2] };
          $( '#rgb-image-space-bands' ).show();
          $( '#gray-image-space-bands' ).hide();
       }

        if ( bands[0] == 'default' ) {
            $( '#rgb-image-space-bands' ).hide();
            $( '#gray-image-space-bands' ).hide();

            $scope.grayImageItem = { 'key': 1, 'value': 1 };
            $scope.redImageItem = { 'key': 1, 'value': 1 };
            $scope.greenImageItem = { 'key': 2, 'value': 2 };
            $scope.blueImageItem = { 'key': 3, 'value': 3 };

            $scope.bandTypeItem = $scope.bandTypeValues[0];
        }
      }

    $scope.onBandSelect = function( selectedValue, selectedBand ) {
      switch ( selectedBand.toUpperCase() ){
        case 'RED':
          $scope.rgbValues.red = selectedValue;
          bands = $scope.rgbValues.red + ',' + $scope.rgbValues.green + ',' + $scope.rgbValues.blue;
        break;
        case 'GREEN':
          $scope.rgbValues.green = selectedValue;
          bands = $scope.rgbValues.red + ',' + $scope.rgbValues.green + ',' + $scope.rgbValues.blue;
        break;
        case 'BLUE':
          $scope.rgbValues.blue = selectedValue;
          bands = $scope.rgbValues.red + ',' + $scope.rgbValues.green + ',' + $scope.rgbValues.blue;
        break;
        case 'GRAY':
          $scope.grayValue = selectedValue;
          bands = $scope.grayValue;
        break;
      }
      imageSpaceService.setBands( bands );
    };

    $scope.showBands = function( bandType ) {
      switch ( bandType.toUpperCase() ){
        case 'COLOR':
        bands = $scope.rgbValues.red + ',' + $scope.rgbValues.green + ',' + $scope.rgbValues.blue;
        imageSpaceService.setBands( bands );
          $( '#rgb-image-space-bands' ).show();
          $( '#gray-image-space-bands' ).hide();
        break;
        case 'GRAY':
        if ( $scope.grayValue ) {
          bands = $scope.grayValue;
        } else {
          bands = 1;
        }
        imageSpaceService.setBands( bands );
          $( '#gray-image-space-bands' ).show();
          $( '#rgb-image-space-bands' ).hide();
        break;
        case 'DEFAULT':
        imageSpaceService.setBands( 'default' );
          $( '#rgb-image-space-bands' ).hide();
          $( '#gray-image-space-bands' ).hide();
        break;
      }
    };

    //END - Band Selection Section

    // Start - Brightness/Contrast Section

    // Instantiate a slider
     brightnesSlider = $( '#imgBrightnessSlider' ).slider({
         value: parseFloat( brightness ),
         min: -1.0,
         max: 1.0,
         precision: 2,
         step: 0.01,
         tooltip: 'hide'
     });

     contrastSlider = $( '#imgContrastSlider' ).slider({
         value: parseFloat( contrast ),
         min: 0.01,
         max: 20.0,
         precision: 2,
         step: 0.01,
         tooltip: 'hide'
     });

     $( '#imgBrightnessVal' ).text( brightness );

     brightnesSlider.on( 'slide', function( slideEvt ) {
       $( '#imgBrightnessVal' ).text( slideEvt.value );
     });

     brightnesSlider.on( 'slideStop', function( slideEvt ) {
       imageSpaceService.setBrightness( slideEvt.value );
       $( '#imgBrightnessVal' ).text( slideEvt.value );
     });

     $( '#imgContrastVal' ).text( parseFloat( contrast ) );

     contrastSlider.on( 'slide', function( slideEvt ) {
       $( '#imgContrastVal' ).text( slideEvt.value );
     });

     contrastSlider.on( 'slideStop', function( slideEvt ) {
       imageSpaceService.setContrast( slideEvt.value );
       $( '#imgContrastVal' ).text( slideEvt.value );
     });

     vm.resetBrightnessContrast = function() {
         $( '#imgBrightnessVal' ).text( imageSpaceObj.brightness );
         brightnesSlider.slider( 'setValue', parseFloat( imageSpaceObj.brightness ) );
         imageSpaceService.setBrightness( imageSpaceObj.brightness );

         $( '#imgContrastVal' ).text( imageSpaceObj.contrast );
         contrastSlider.slider( 'setValue', parseFloat( imageSpaceObj.contrast ) );
         imageSpaceService.setContrast( imageSpaceObj.contrast );
     };

    //END - Brightness/Contrast Section

    // START - Dynamic Range Section

    $scope.draType = {};
    $scope.draTypes = [
        { 'name': 'None', 'value': 'none' },
        { 'name': 'Auto', 'value': 'auto-minmax' },
        { 'name': '1 STD', 'value': 'std-stretch-1' },
        { 'name': '2 STD', 'value': 'std-stretch-2' },
        { 'name': '3 STD', 'value': 'std-stretch-3' }
    ];

    $scope.draType = $scope.draTypes[1];

    angular.forEach( $scope.draTypes, function( value, key ) {
      if ( value.value == imageSpaceObj.histOp ) {
        $scope.draType = { 'name': value.name, 'value': value.value };
      }// end if
    }); //end foreach

    $scope.onDraSelect = function( value ) {
        imageSpaceService.setDynamicRange( value );
    };

    $scope.draRegionType = {};
    $scope.draRegionTypes = [
        { 'name': 'Global', 'value': 'false' },
        { 'name': 'Viewport', 'value': 'true' }
    ];

    $scope.draRegionType = $scope.draRegionTypes[1];

    angular.forEach( $scope.draRegionTypes, function( value, key ) {
      if ( value.value == imageSpaceObj.histCenterTile ) {
        $scope.draRegionType = { 'name': value.name, 'value': value.value };
      }
    });

    $scope.onDraRegionSelect = function( value ) {
        imageSpaceService.setDynamicRangeRegion( value );
    };

    $scope.resamplerFilterType = {};
    $scope.resamplerFilterTypes = [
        { 'name': 'Bessel', 'value': 'bessel' },
        { 'name': 'Bilinear', 'value': 'bilinear' },
        { 'name': 'Blackman', 'value': 'blackman' },
        { 'name': 'B-Spline', 'value': 'bspline' },
        { 'name': 'Catrom', 'value': 'catrom' },
        { 'name': 'Cubic', 'value': 'cubic' },
        { 'name': 'Gaussian', 'value': 'gaussian' },
        { 'name': 'Hamming', 'value': 'hamming' },
        { 'name': 'Hermite', 'value': 'hermite' },
        { 'name': 'Lanczos', 'value': 'lanczos' },
        { 'name': 'Magic', 'value': 'magic' },
        { 'name': 'Mitchell', 'value': 'mitchell' },
        { 'name': 'Nearest', 'value': 'nearest' },
        { 'name': 'Quadratic', 'value': 'quadratic' },
        { 'name': 'Sinc', 'value': 'sinc' }
    ];

    $scope.resamplerFilterType = $scope.resamplerFilterTypes[1];

    angular.forEach( $scope.resamplerFilterTypes, function( value, key ) {
      if ( value.value == imageSpaceObj.resamplerFilter ) {
        $scope.resamplerFilterType = { 'name': value.name, 'value': value.value };
      }// end if
    }); //end foreach

    $scope.onResamplerFilterSelect = function( value ) {
        imageSpaceService.setResamplerFilter( value );
    };

    $scope.sharpenModeType = {};
    $scope.sharpenModeTypes = [
        { 'name': 'None', 'value': 'none' },
        { 'name': 'Light', 'value': 'light' },
        { 'name': 'Heavy', 'value': 'heavy' }
    ];
    $scope.sharpenModeType = $scope.sharpenModeTypes[0];

    angular.forEach( $scope.sharpenModeTypes, function( value, key ) {
      if ( value.value == imageSpaceObj.sharpenMode ) {
        $scope.sharpenModeType = { 'name': value.name, 'value': value.value };
      }// end if
    }); //end foreach

    $scope.onSharpenModeSelect = function( value ) {
        imageSpaceService.setSharpenMode( value );
    };

    function loadMapImage() {
      brightness = parseFloat( $stateParams.brightness );
      contrast = parseFloat( $stateParams.contrast );

      imageSpaceObj = {
          filename: $stateParams.filename,
          entry: $stateParams.entry_id,
          imgWidth: $stateParams.width,
          imgHeight: $stateParams.height,
          numOfBands: $stateParams.numOfBands,
          bands: $stateParams.bands,
          imageId: $stateParams.imageId,
          url: $stateParams.ur,
          brightness: brightness,
          contrast: contrast,
          histOp: $stateParams.histOp,
          histCenterTile: $stateParams.histCenterTile,
          resamplerFilter: $stateParams.resamplerFilter,
          sharpenMode: $stateParams.sharpenMode
        };

        vm.imageMapPath = AppO2.APP_CONFIG.serverURL + '/omar/#/mapImage?filename=' +
                          imageSpaceObj.filename +  '&entry_id=' +
                          imageSpaceObj.entry +  '&width=' +
                          imageSpaceObj.imgWidth +  '&height=' +
                          imageSpaceObj.imgHeight +  '&bands=' +
                          imageSpaceObj.bands +  '&numOfBands=' +
                          imageSpaceObj.numOfBands +  '&imageId=' +
                          imageSpaceObj.imageId + '&brightness=' +
                          imageSpaceObj.brightness + '&contrast=' +
                          imageSpaceObj.contrast + '&histOp=' +
                          imageSpaceObj.histOp + '&histCenterTile=' +
                          imageSpaceObj.histCenterTile + '&resamplerFilter' +
                          imageSpaceObj.resamplerFilter + '&sharpenMode'
                          imageSpaceObj.sharpenMode;

      // Pass our imageSpaceObj constructed from the UR
      // ($stateParams) into the imageSpaceService and load
      // the map.
      imageSpaceService.initImageSpaceMap( imageSpaceObj );

    }

    // Begin - Measurment Section

    $scope.itemMeasureTypeArray = [
      {id: 1, name: 'meters', value: 'm'},
      {id: 2, name: 'kilometers', value: 'km'},
      {id: 3, name: 'feet', value: 'ft'},
      {id: 4, name: 'miles', value: 'mi'},
      {id: 5, name: 'yards', value: 'yd'},
      {id: 6, name: 'nautical miles', value: 'nmi'},
    ];

    $scope.selectedMeasureType = { value: $scope.itemMeasureTypeArray[0] };

    vm.measureMessage = 'Choose a measure type from the toolbar';
    vm.measureType = 'None';

    function setMeasureUiComponents(){

      vm.showMeasureInfo = false;
      vm.measureType = 'None';
      vm.measureMessage = 'Choose a measure type from the toolbar';
      vm.displayArea = false;
      vm.displayAzimuth = false;
      vm.geodDist = '';
      vm.recDist = '';
      vm.azimuth = '';
      vm.area = '';

    }

    function changeMeasureOutputSystem(data, type){

      function linearCalc(val, multiplier){
        return (val * multiplier).toFixed(4);
      }

      function areaCalc(val, multiplier){
        if(!data.area){
          return
        } else {
          return (val * multiplier).toFixed(4);
        }
      }

      if (data) {
        switch (type){
          case 'm':
            vm.geodDist = linearCalc(data.gdist, 1) + ' ' + type;
            vm.recDist = linearCalc(data.distance, 1) + ' ' + type;
            vm.area = areaCalc(data.area, 1) + " m^2";
          break;
          case 'km':
            vm.geodDist = linearCalc(data.gdist, 0.001) + ' ' + type;
            vm.recDist = linearCalc(data.distance, 0.001) + ' ' + type;
            vm.area = areaCalc(data.area, 0.000001) + " km^2";
          break;
          case 'ft':
            vm.geodDist = linearCalc(data.gdist, 3.280839895) + ' ' + type;
            vm.recDist = linearCalc(data.distance, 3.280839895) + ' ' + type;
            vm.area = areaCalc(data.area, 10.763910416623611025) + " ft^2";
          break;
          case 'mi':
            vm.geodDist = linearCalc(data.gdist, 0.00062137119224) + ' ' + type;
            vm.recDist = linearCalc(data.distance, 0.00062137119224) + ' ' + type;
            vm.area = areaCalc(data.area, .00000038610215854575) + " mi^2";
          break;
          case 'yd':
            vm.geodDist = linearCalc(data.gdist, 1.0936132983) + ' ' + type;
            vm.recDist = linearCalc(data.distance, 1.0936132983) + ' ' + type;
            vm.area = areaCalc(data.area, 1.19598861218942) + " yd^2";
          break;
          case 'nmi':
            vm.geodDist = linearCalc(data.gdist, 0.000539957) + ' ' + type;
            vm.recDist = linearCalc(data.distance, 0.000539957) + ' ' + type;
            vm.area = areaCalc(data.area, .000000291553) + " nmi^2";
          break;
        }

        // Azimuth calcuation on LineString
        if (data.azimuth) {
          vm.displayAzimuth = true;
          vm.azimuth = data.azimuth.toFixed(3) + ' deg';
        }
        else if (!data.azimuth) {
          vm.displayAzimuth = false;
          vm.azimuth = '0';
        }

        // Area calculation on Polygons
        if(data.area) {
          vm.displayArea = true;
          //vm.area = Math.round(data.area*1000)/1000 + ' m';;
        }
        else if (!data.area) {
          vm.displayArea = false;
          vm.area = '0';
        }
      }
      else {
        vm.geodDist = "";
        vm.recDist = "";
        vm.area = "";
      }
    }

    vm.measure = function(show, type) {

      vm.pqeClear();

      imageSpaceService.pqeClear();
      vm.pqeShowInfo = false;

      switch (type){
        case 'LineString':
          vm.measureType = 'Path';
          vm.measureShow = true;
          imageSpaceService.measureActivate(type);
          vm.measureLine = true;
          vm.measurePolygon = false;
        break;
        case 'Polygon':
          vm.measureType = 'Area';
          vm.measureShow = true;
          imageSpaceService.measureActivate(type);
          vm.measureLine = false;
          vm.measurePolygon = true;
        break;
      }

      vm.showMeasureInfo = true;
      vm.measureMessage = 'Click in the map to begin the measurement';

    }

    vm.setMeasureUnits = function(measureType) {

      // Only calculate the measurement if we have a valid measure object
      if(angular.equals(measureDataObj, {})) {
        return;
      } else {
        changeMeasureOutputSystem(measureDataObj, measureType);
      }
    }

    vm.measureClear = function() {

      vm.measureShow = true;
      imageSpaceService.measureClear();

      // Reset the UI to original state
      setMeasureUiComponents();

    }

    var measureDataObj = {};

    $scope.$on('measure: updated', function(event, data) {

      measureDataObj = data;
      changeMeasureOutputSystem(measureDataObj, $scope.selectedMeasureType.value.value);

    });

    // End - Measurement Section

    // Begin Position Quality Evaluator Section

    $scope.pqeProbabilityArray = [
      {id: 1, name: '0.9P', value: '0.9'},
      {id: 2, name: '0.95P', value: '0.95'},
      {id: 3, name: '0.5P', value: '0.5'},
    ];

    $scope.selectedProbabilityType = { value: $scope.pqeProbabilityArray[0] };

    vm.pqeMessage = 'Click in the map to add a point. The position and the error of the information associated with it will be displayed.';
    vm.showPqePosOutput = false;
    vm.showPqeOutput = false;
    vm.showPqeWarning = false;

    vm.pqe = function(probability){

      vm.measureClear();

      vm.pqeShowInfo = true;

      imageSpaceService.pqeActivate();

    }

    vm.setPqeProbability = function(value) {

      imageSpaceService.setPqeProbability(value);

    }

    vm.pqeClear = function(){


      vm.pqeShowInfo = false;

      vm.showPqeOutput = false;

      vm.pqeMessage = 'Click in the map to add a point. The position and the error of the information associated with it will be displayed.';
      vm.ce = '';
      vm.le = '';
      vm.sma = '';
      vm.smi = '';
      vm.az = '';
      vm.projType = '';
      vm.surfaceName = '';
      vm.lvl = '';

      vm.showPqeWarning = false;

      vm.showPqePosOutput = false;

      vm.lat = '';
      vm.lon = '';
      vm.hgt = '';
      vm.hgtMsl = '';
      vm.imageX = '';
      vm.imageY = '';

      imageSpaceService.pqeClear();

    }

    var pqeObj = {};
    $scope.$on('pqe: updated', function(event, data) {

      pqeObj = data[0];

      if (pqeObj.pqe.pqeValid){

        vm.showPqeOutput = true;

        vm.pqeMessage = 'The information below illustrates the position of the clicked point in the map.  The cyan point and ellipsis around it display the probabilty of error for the point calculation.'
        vm.ce = pqeObj.pqe.CE.toFixed(4);
        vm.le = pqeObj.pqe.LE.toFixed(4) + ' m';
        vm.sma = pqeObj.pqe.SMA.toFixed(4);
        vm.smi = pqeObj.pqe.SMI.toFixed(4) + ' m';
        vm.az = pqeObj.pqe.AZ.toFixed(4) + ' deg';
        vm.projType = pqeObj.pqe.projType;
        vm.surfaceName = pqeObj.pqe.surfaceName;
        vm.lvl = pqeObj.pqe.probabilityLevel.toFixed(1) + 'P';

      }
      else {

        vm.showPqeWarning = true;
        vm.pqeMessage = '';

      }

      vm.showPqePosOutput = true;

      vm.lat = pqeObj.lat.toFixed(7);
      vm.lon = pqeObj.lon.toFixed(7);
      vm.hgt = pqeObj.hgt.toFixed(4);
      vm.hgtMsl = pqeObj.hgtMsl.toFixed(4) + ' m';
      vm.imageX = pqeObj.x.toFixed(4);
      vm.imageY = pqeObj.y.toFixed(4);

    });

    // End Position Quality Evaluator Section

    vm.screenshot = function() { imageSpaceService.screenshot(); }
    vm.viewMetadata = function( image ) {
        var url = AppO2.APP_CONFIG.params.wfs.baseUrl +
            "filter=in(" + image.imageId + ")&" +
            "request=GetFeature&" +
            "service=WFS&" +
            "typeName=omar:raster_entry&" +
            "version=1.1.0";
        window.open( url );
    }
    vm.zoomToFullExtent = function() { imageSpaceService.zoomToFullExtent(); }
    vm.zoomToFullRes = function() { imageSpaceService.zoomToFullRes(); }

  }

}() );
