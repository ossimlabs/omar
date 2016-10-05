(function () {
  'use strict';
  angular
    .module('omarApp')
    .controller('MapController', ['mapService', '$stateParams', '$scope', 'toastr', MapController]);

  function MapController(mapService, $stateParams, $scope, toastr) {

    toastr.info("Click on the thumbnail or ID text in the image card to view the image and it's" +
      " metadata", 'Heads Up:', {
      positionClass: 'toast-bottom-left',
      closeButton: true,
      timeOut: 10000,
      extendedTimeOut: 5000,
      target: 'body'
    });

    mapService.mapInit();

    $scope.$on('attrObj.updated', function (event, filter) {

      mapService.updateFootPrintLayer(filter);

    });

    $scope.$on('mapState.updated', function (event, params) {

      mapService.zoomMap(params);

    });

  }
})();
