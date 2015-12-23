(function(){
   'use strict';
    angular
        .module('omarApp')
        .controller('FilterController', ['$scope', 'wfsService', FilterController]);

        function FilterController($scope, wfsService){

            /* jshint validthis: true */
            var vm = this;

            vm.predNiirsCheck = false;
            vm.predNiirsMin = "0.0";
            vm.predNiirsMax = "9.0";

            vm.azimuthCheck = false;
            vm.azimuthMin = "0.0";
            vm.azimuthMax = "360";

            //console.log('vm.predNiirsMin', vm.predNiirsMin);

            // TODO: Set the ng-model for the filter items

            vm.filterWfs = function() {

                console.log('filterWfs firing!');
                console.log(vm.predNiirsMin);

                wfsService.executeWfsQuery();

            };

        }
})();
