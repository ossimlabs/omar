/**
 * Created by adrake on 10/19/15.
 */
(function(){
angular
    .module('omarApp')
    .controller('ListController', ListController);

    function ListController(appConfigService, wfsService, $stateParams, $log){

        /* jshint validthis: true */
        var vm = this;
        var config;
        vm.title = "ListController";

        var configPromise = appConfigService.getConfig();
        configPromise.then(function(data){
            config = data;
            console.log(config);
            wfsRequest();
        });
        //console.log('config', configPromise);

        //vm.test = $stateParams.test;
        //$log.warn('stateParams', vm.test);

        function wfsRequest(){

            console.log('config in wfsRequest', config);
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

    }
})();