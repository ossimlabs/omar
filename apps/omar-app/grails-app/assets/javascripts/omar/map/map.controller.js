(function () {
  'use strict';
  angular
    .module('omarApp')
    .controller('MapController', ['mapService', '$stateParams', '$scope', 'toastr', MapController]);

  function MapController(mapService, $stateParams, $scope, toastr) {

    // toastr.info("Click on the thumbnail or ID text in the image card to view the image and it's" +
    //     " metadata", 'Heads Up:', {
    //     positionClass: 'toast-bottom-left',
    //     closeButton: true,
    //     timeOut: 10000,
    //     extendedTimeOut: 5000,
    //     target: 'body'
    // });

    /* jshint validthis: true */
    var vm = this;

    //console.log('$stateParams.mapParams', $stateParams.mapParams);

    if ($stateParams.mapParams === 'mapParamsDefaultMap') {

      //console.log('Default...');
      mapService.mapInit(vm.mapParams);

    } else {

      vm.mapParams = JSON.parse($stateParams.mapParams);
      mapService.zoomMap(vm.mapParams);
      console.log('stateParams.mapParams: ', $stateParams.mapParams);

    }

    //console.log(vm.mapParams);
    //mapService.mapInit(vm.mapParams);

    $scope.$on('attrObj.updated', function (event, filter) {

      //console.log('$on attrObj filter updated', filter);
      mapService.updateFootPrintLayer(filter);

    });

    $scope.$on('mapState.updated', function(event, params) {

        // Update the DOM (card list)
        $scope.$apply(function() {

          console.log('We are in the mapState: updated $on');
          console.log('params => ', params);
          mapService.zoomMap(params);

        });

    });

  }
})();
