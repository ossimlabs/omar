/**
 * Created by adrake on 10/30/15.
 */
(function(){
    angular
        .module('omarApp')
        .controller('MapController', MapController);

    function MapController($log){

        /* jshint validthis: true */
        var vm = this;
        vm.title = "MapController";

    }
})();
