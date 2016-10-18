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

    //Beginning - Band Selections Section

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

    $scope.asideState = {
      open: false
    };

    $scope.openAside = function(position, backdrop) {
      $scope.asideState = {
        open: true,
        position: position
      };

      function postClose() {
        $scope.asideState.open = false;
      }

      $aside.open({
        templateUrl: AppO2.APP_CONFIG.serverURL + '/mapImage/aside.html',
        placement: position,
        size: 'sm',
        backdrop: false,
        controller: function ($scope, $uibModalInstance) {
          $scope.ok = function(e) {
            $uibModalInstance.close();
            e.stopPropagation();
          };

          $scope.cancel = function (e) {
            $uibModalInstance.dismiss();
            e.stopPropagation();
          };
        }
      }).result.then(postClose, postClose);
    };

  }

}() );
