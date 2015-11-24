/**
 * Created by adrake on 10/30/15.
 */
(function(){
    angular
        .module('omarApp')
        .controller('MapController', MapController);

    function MapController(mapService, $stateParams){

        /* jshint validthis: true */
        var vm = this;
        vm.mapTitle = "Map";
        vm.listTitle = "Images";

        // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
        console.log('$stateParams', $stateParams);
        if ($stateParams.mapParams === 'mapParamsDefaultMap'){

            console.log('DEFAULT!!!');

        }
        else {

            vm.mapParams = JSON.parse($stateParams.mapParams);

        }
        //console.log(vm.mapParams);

        mapService.mapInit(vm.mapParams);

    }
})();
