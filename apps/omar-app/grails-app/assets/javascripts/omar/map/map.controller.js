(function(){
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

            /* jshint validthis: true */
            var vm = this;
            //vm.mapTitle = "Map";
            //vm.listTitle = "Images";

            // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
            //console.log('$stateParams', $stateParams);
            if ($stateParams.mapParams === 'mapParamsDefaultMap') {

                //console.log('Default...');

            }
            else {

                vm.mapParams = JSON.parse($stateParams.mapParams);

            }

            //console.log(vm.mapParams);
            mapService.mapInit(vm.mapParams);

            $scope.$on('attrObj.updated', function(event, filter) {

                console.log('$on attrObj filter updated', filter);
                mapService.updateFootPrintLayer(filter);

            });

        }
})();
