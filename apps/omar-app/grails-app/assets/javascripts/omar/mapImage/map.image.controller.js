(function () {
  'use strict';
  angular
  .module('omarApp')
  .controller('MapImageController', ['$scope', '$aside', '$state', '$stateParams',
    '$location', 'toastr', 'imageSpaceService', 'beNumberService', MapImageController]);

  function MapImageController($scope, $aside, $state, $stateParams, $location, toastr,
     imageSpaceService, beNumberService) {

    var vm = this;

    var imageSpaceObj = {};

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

    }

    checkStateParams();

    function loadMapImage() {

      imageSpaceObj = {
          filename: $stateParams.filename,
          entry: $stateParams.entry_id,
          imgWidth: $stateParams.width,
          imgHeight: $stateParams.height
        };

      // Pass our imageSpaceObj constructed from the URL
      // ($stateParams) into the imageSpaceService and load
      // the map.
      imageSpaceService.initImageSpaceMap(imageSpaceObj);

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
    }

  }

}());
