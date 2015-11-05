/**
 * Created by adrake on 10/19/15.
 */
(function(){
angular
    .module('omarApp')
    .controller('ListController', ListController);

    function ListController(wfsService, $log, $stateParams){

        /* jshint validthis: true */
        var vm = this;
        vm.title = "ListController";

        //vm.test = $stateParams.test;
        //$log.warn('stateParams', vm.test);

        var wfsRequestObj = {};

        wfsRequestObj.maxFeatures = $stateParams.maxFeatures;
        wfsRequestObj.cql = $stateParams.cql;

        wfsService.executeWfsQuery(wfsRequestObj);

        var promise = wfsService.getWfsResults();

        promise.then(function(data){
            vm.wfsData = data;
            //$log.warn('wfsData', vm.wfsData);
            //$log.warn('wfsData.length', vm.wfsData.length);
        });

    }
})();