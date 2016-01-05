(function(){
   'use strict';
    angular
        .module('omarApp')
        .controller('FilterController', ['wfsService', FilterController]);

        function FilterController(wfsService){

            /* jshint validthis: true */
            var vm = this;

            // Keywords
            vm.missionIdCheck = false;
            vm.missionId = "";

            vm.sensorIdCheck = false;
            vm.sensorId = "";

            vm.beNumberCheck = false;
            vm.beNumber = "";

            vm.targetIdCheck = false;
            vm.targetId = "";

            vm.wacNumberCheck = false;
            vm.wacNumber = "";

            vm.filenameCheck = false;
            vm.filename = "";

            vm.imageIdCheck = false;
            vm.imageId = "";

            // Ranges
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

            var filterString = "";

            function updateFilterString(){

                var filterArray =[];

                function pushKeywordToArray(dbName, formField){

                    // TODO: Always assume contains and case insensitive

                    filterArray.push([dbName + " Like '%", formField.trim() ,"%'"].join(""));
                    console.log(dbName + ' filterArray', filterArray);

                }

                function pushRangeToArray(dbName, formFieldMin, formFieldMax){

                    filterArray.push([dbName,  ">=", formFieldMin, "AND", dbName, "<=",  formFieldMax].join(" "));
                    console.log(dbName + 'filterArray', filterArray);

                }

                // Keywords
                if(vm.missionIdCheck){
                    //filterArray.push(["mission_id Like '%", vm.missionId.trim() ,"%'"].join(""));
                    pushKeywordToArray("mission_id", vm.missionId);
                    //console.log('vm.missionIdCheck filterArray', filterArray);
                }
                if(vm.sensorIdCheck){
                    //filterArray.push(["sensor_id Like '%", vm.sensorId.trim(), "%'"].join(""));
                    pushKeywordToArray("sensor_id", vm.sensorId);
                    //console.log('vm.sensorIdCheck filterArray', filterArray);
                }
                if(vm.beNumberCheck){
                    //filterArray.push(["be_number Like '%", vm.beNumber.trim(), "%'"].join(""));
                    pushKeywordToArray("be_number", vm.beNumber);
                    //console.log('vm.be_number filterArray', filterArray);
                }
                if(vm.targetIdCheck){
                    //filterArray.push(["target_id Like '%", vm.targetId.trim(), "%'"].join(""));
                    pushKeywordToArray("target_id", vm.targetId);
                    //console.log('vm.target_id filterArray', filterArray);
                }
                if(vm.wacNumberCheck){
                    //filterArray.push(["wac_code Like '%", vm.wacNumber.trim(), "%'"].join(""));
                    pushKeywordToArray("wac_code", vm.wacNumber);
                    //console.log('vm.wac_code filterArray', filterArray);
                }
                if(vm.filenameCheck){
                    //filterArray.push(["filename Like '%", vm.filename.trim(), "%'"].join(""));
                    pushKeywordToArray("filename", vm.filename);
                    //console.log('vm.filename filterArray', filterArray);
                }
                if(vm.imageIdCheck){
                    //filterArray.push(["title Like '%", vm.imageId.trim(), "%'"].join(""));
                    pushKeywordToArray("title", vm.imageId);
                    //console.log('vm.imageId filterArray', filterArray);
                }

                // Ranges
                if (vm.predNiirsCheck){

                    //filterArray.push(["niirs",  ">=", vm.predNiirsMin, "AND", "niirs", "<=",
                    //  vm.predNiirsMax].join(" "));
                    pushRangeToArray("niirs", vm.predNiirsMin, vm.predNiirsMax);

                }
                if (vm.azimuthCheck){

                    //filterArray.push(["azimuth_angle",  ">=", vm.azimuthMin, "AND", "azimuth_angle", "<=",
                    //  vm.azimuthMax].join(" "));
                    pushRangeToArray("azimuth_angle", vm.azimuthMin, vm.azimuthMax);

                }
                if (vm.grazeElevCheck){

                    filterArray.push(["grazing_angle",  ">=", vm.grazeElevMin, "AND", "grazing_angle", "<=",  vm.grazeElevMax].join(" "));

                }
                if (vm.sunAzimuthCheck){

                    filterArray.push(["sun_azimuth",  ">=", vm.sunAzimuthMin, "AND", "sun_azimuth", "<=",  vm.sunAzimuthMax].join(" "));

                }
                if (vm.sunElevationCheck){

                    filterArray.push(["sun_elevation",  ">=", vm.sunElevationMin, "AND", "sun_elevation", "<=",  vm.sunElevationMax].join(" "));

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
