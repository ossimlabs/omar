/**
 * Created by adrake on 10/30/15.
 */
(function(){
    angular
        .module('omarApp')
        .controller('MapController', MapController);

    function MapController(mapService, $log, $stateParams){

        /* jshint validthis: true */
        var vm = this;
        vm.mapTitle = "Map";
        vm.listTitle = "Images";

        // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
        vm.mapParams = JSON.parse($stateParams.mapParams);
        console.log(vm.mapParams);

        //-----------------------

         //mapService.mapServiceTest();

    }
})();
