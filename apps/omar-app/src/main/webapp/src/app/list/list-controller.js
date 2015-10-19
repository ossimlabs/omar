/**
 * Created by adrake on 10/19/15.
 */
(function(){
angular
    .module('omarApp')
    .controller('ListController', ListController);

    function ListController(wfsService, $log){

        /* jshint validthis: true */
        var vm = this;
        vm.title = "ListController"

        var promise = wfsService.getWfs();

        promise.then(function(data){
            vm.wfsData = data;
            $log.warn('wfsData', vm.wfsData);
            $log.warn('wfsData.length', vm.wfsData.length);
        });

    }
})();