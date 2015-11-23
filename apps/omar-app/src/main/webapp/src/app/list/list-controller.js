/**
 * Created by adrake on 10/19/15.
 */
(function(){
angular
    .module('omarApp')
    .controller('ListController', ListController);

    function ListController(APP_CONFIG, wfsService, $stateParams, $log){

        /* jshint validthis: true */
        var vm = this;

        vm.omarUrl = APP_CONFIG.services.omar.url;
        vm.omarThumbnails = APP_CONFIG.services.omar.thumbnailsUrl;

        vm.title = "ListController";

        //console.log('vm.omar', vm.omarUrl);
        //console.log('vm.omarThumbnail', vm.omarThumbnails);

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
