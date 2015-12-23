(function(){
   'use strict';
    angular
        .module('omarApp')
        .controller('FilterController', ['$scope', 'wfsService', FilterController]);

        function FilterController($scope, wfsService){

            /* jshint validthis: true */
            var vm = this;

            vm.predNiirsCheck = true;
            vm.predNiirsMin = "0.0";
            vm.predNiirsMax = "9.0";

            vm.azimuthCheck = true;
            vm.azimuthMin = "0";
            vm.azimuthMax = "360";

            vm.grazeElevCheck = true;
            vm.grazeElevMin = "0.0";
            vm.grazeElevMax = "90.0";

            var filterString = "";

            function updateFilterString(){

                var filterArray =[];

                if (vm.predNiirsCheck){

                    console.log('vm.predNiirsCheck true...');
                    //filterArray.push("niirs Between " + vm.predNiirsMin +  " AND " + vm.predNiirsMax);
                    filterArray.push(["niirs",  ">=", vm.predNiirsMin,   "AND", "niirs", "<=",  vm.predNiirsMax].join(" "));
                }
                if (vm.azimuthCheck){
                    if (filterString )
                    console.log('vm.azimuthCheck true...');
                    filterArray.push(["azimuth_angle",  ">=", vm.azimuthMin,   "AND", "azimuth_angle", "<=",  vm.azimuthMax].join(" "));

                }
                if (vm.grazeElevCheck){

                    console.log('vm.grazeElevMin true...');
                    //filterArray.push("grazing_angle >= " + vm.grazeElevMin +  " AND " + "azimuth_angle" +
                    //    " =< " + vm.grazeElevMax);
                    filterArray.push(["grazing_angle",  ">=", vm.grazeElevMin,   "AND", "grazing_angle", "<=",  vm.grazeElevMax].join(" "));

                }

                filterString = filterArray.join(" AND ");
                console.log(filterString);
                return filterString;

            }

            vm.filterWfs = function() {

                //console.log('filterWfs firing!');
                var filterStringParam = updateFilterString();

                wfsService.executeWfsQuery(null, filterStringParam);

                // TODO add pubsub patter here like the wfs update

            };

        }
})();
