(function () {
  'use strict';
  angular
  .module('omarApp')
  .controller('MapImageController', ['$scope', '$aside', '$state', '$stateParams',
    '$location', 'toastr', 'imageSpaceService', 'beNumberService', 'downloadService',
    'shareService', MapImageController]);

  function MapImageController( $scope, $aside, $state, $stateParams, $location, toastr,
     imageSpaceService, beNumberService, downloadService, shareService ) {

    var vm = this;

    var imageSpaceObj = {};

    //Used by band selection
    var bands, numberOfBands, bandNum,
    redSelect, greenSelect, blueSelect;

    vm.baseServerUrl = AppO2.APP_CONFIG.serverURL;

    vm.shareModal = function( imageLink ) {
      shareService.imageLinkModal( imageLink );
    };

    vm.archiveDownload = function( imageId ) {
      downloadService.downloadFiles( imageId );
    };

    function checkStateParams() {

      // Check to make sure that all of the $stateParams are defined.
      // If there are undefined params return an error.
      for (var i in $stateParams) {

        if ($stateParams[i] === undefined) {

          toastr.error('There was an issue loading the selected image into the map.',
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

    // Begin - Band Selections Section

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
          $scope.bandTypeItem = $scope.bandTypeValues[0];

          if ( numberOfBands == 2 ) {
            $scope.enableBandType = true;
          } else {
            $scope.enableBandType = false;
          }

          if ( numberOfBands <= 1 ) {
            $( '#gray-image-space-bands' ).hide();
          }else {
            $( '#gray-image-space-bands' ).show();
          }

          $( '#rgb-image-space-bands' ).hide();

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
          $scope.bandTypeItem = $scope.bandTypeValues[0];
        }

        if ( bands[0] == 'default' ) {
            $( '#rgb-image-space-bands' ).hide();
            $( '#gray-image-space-bands' ).hide();
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
        if($scope.grayValue) {
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

    // START - Dynamic Range Section
    $scope.draType = {};
    $scope.draTypes = [
        { 'name': 'None' , 'value': 'none' },
        { 'name': 'Auto', 'value': 'auto-minmax' },
        { 'name': '1 STD', 'value': 'std-stretch-1' },
        { 'name': '2 STD', 'value': 'std-stretch-2' },
        { 'name': '3 STD', 'value': 'std-stretch-3' }
    ];
    $scope.draType = $scope.draTypes[1];

    $scope.onDraSelect = function( draValue ) {
        imageSpaceService.setDynamicRange( draValue );
    };
    // END - Dynamic Range Section

    function loadMapImage() {

      imageSpaceObj = {
          filename: $stateParams.filename,
          entry: $stateParams.entry_id,
          imgWidth: $stateParams.width,
          imgHeight: $stateParams.height,
          numOfBands: $stateParams.numOfBands,
          bands: $stateParams.bands,
          imageId: $stateParams.imageId,
          url: $stateParams.ur
        };

        vm.imageMapPath = AppO2.APP_CONFIG.serverURL + '/omar/#/mapImage?filename=' +
                          imageSpaceObj.filename +  '&entry_id=' +
                          imageSpaceObj.entry +  '&width=' +
                          imageSpaceObj.imgWidth +  '&height=' +
                          imageSpaceObj.imgHeight +  '&bands=' +
                          imageSpaceObj.bands +  '&numOfBands=' +
                          imageSpaceObj.numOfBands +  '&imageId=' +
                          imageSpaceObj.imageId;
      // Pass our imageSpaceObj constructed from the UR
      // ($stateParams) into the imageSpaceService and load
      // the map.
      imageSpaceService.initImageSpaceMap( imageSpaceObj );

    }

    // Begin - Measurment Section

    $scope.itemArray = [
      {id: 1, name: 'meters', value: 'm'},
      {id: 2, name: 'kilometers', value: 'km'},
      {id: 3, name: 'feet', value: 'ft'},
      {id: 4, name: 'miles', value: 'mi'},
      {id: 5, name: 'yards', value: 'yd'},
      {id: 6, name: 'nautical miles', value: 'nmi'},
    ];

    $scope.selected = { value: $scope.itemArray[0] };

    vm.measureMessage = 'Choose a measure type from the toolbar';
    vm.measureType = 'None';

    function setMeasureUiComponents(){

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
          vm.area = areaCalc(data.area, 10.7639) + " ft^2";
        break;
        case 'mi':
          vm.geodDist = linearCalc(data.gdist, 0.00062137119224) + ' ' + type;
          vm.recDist = linearCalc(data.distance, 0.00062137119224) + ' ' + type;
          vm.area = areaCalc(data.area, .00000386102) + " mi^2";
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

    vm.measure = function(show, type) {

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

      changeMeasureOutputSystem(measureDataObj, $scope.selected.value.value);

    });

    // End - Measurement Section

    vm.zoomToFullExtent = function() { imageSpaceService.zoomToFullExtent(); }
    vm.zoomToFullRes = function() { imageSpaceService.zoomToFullRes(); }

  }

}() );
