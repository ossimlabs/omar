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
            vm.azimuthMin = "0";
            vm.azimuthMax = "360";

            vm.grazeElevCheck = false;
            vm.grazeElevMin = "0.0";
            vm.grazeElevMax = "90.0";

            vm.sunAzimuthCheck = false;
            vm.sunAzimuthMin = "0.0";
            vm.sunAzimuthMax = "360";

            vm.sunElevationCheck = false;
            vm.sunElevationMin = "-90";
            vm.sunElevationMax = "90";

            vm.cloudCoverCheck = false;
            vm.cloudCover = "20";

            //vm.open = function($event) {
            //    vm.status.opened = true;
            //};
            //
            //vm.status = {
            //    opened: false
            //};

            var filterString = "";

            function updateFilterString(){

                var filterArray =[];

                if (vm.predNiirsCheck){

                    filterArray.push(["niirs",  ">=", vm.predNiirsMin,   "AND", "niirs", "<=",  vm.predNiirsMax].join(" "));

                }
                if (vm.azimuthCheck){

                    filterArray.push(["azimuth_angle",  ">=", vm.azimuthMin,   "AND", "azimuth_angle", "<=",  vm.azimuthMax].join(" "));

                }
                if (vm.grazeElevCheck){

                    filterArray.push(["grazing_angle",  ">=", vm.grazeElevMin,   "AND", "grazing_angle", "<=",  vm.grazeElevMax].join(" "));

                }
                if (vm.sunAzimuthCheck){

                    filterArray.push(["sun_azimuth",  ">=", vm.sunAzimuthMin,   "AND", "sun_azimuth", "<=",  vm.sunAzimuthMax].join(" "));

                }
                if (vm.sunElevationCheck){

                    filterArray.push(["sun_elevation",  ">=", vm.sunElevationMin,   "AND", "sun_elevation", "<=",  vm.sunElevationMax].join(" "));

                }
                if (vm.cloudCoverCheck){

                    filterArray.push(["cloud_cover",  "<= " + vm.cloudCover].join(" "));

                }

                filterString = filterArray.join(" AND ");
                console.log(filterString);
                return filterString;

            }

            vm.filterWfs = function() {

                //console.log('filterWfs firing!');
                var filterStringParam = updateFilterString();

                //wfsService.executeWfsQuery(null, filterStringParam); // (currentSpatialFilter, filterStringParam)
                wfsService.updateAttrFilter(filterStringParam);

            };

        }
})();
