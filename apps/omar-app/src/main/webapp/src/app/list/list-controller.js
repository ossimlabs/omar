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

        vm.rate = 7;
        vm.max = 10;
        vm.isReadonly = false;

        vm.hoveringOver = function(value) {
            console.log('hoverOver!');
            vm.overStar = value;
            vm.percent = 100 * (value / vm.max);
        };

        vm.ratingStates = [
            {stateOn: 'glyphicon-ok-sign', stateOff: 'glyphicon-ok-circle'},
            {stateOn: 'glyphicon-star', stateOff: 'glyphicon-star-empty'},
            {stateOn: 'glyphicon-heart', stateOff: 'glyphicon-ban-circle'},
            {stateOn: 'glyphicon-heart'},
            {stateOff: 'glyphicon-off'}
        ];

    }
})();
