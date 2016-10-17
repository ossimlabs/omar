(function() {
    'use strict';
    angular
        .module('omarApp')
        .controller('FilterController', ['wfsService', 'mapService', 'toastr', FilterController]);

    function FilterController(wfsService, mapService, toastr) {

        /* jshint validthis: true */
        var vm = this;

        var filterString = "";
        var filterArray = [];

        vm.initSpatial = function() {

            vm.viewPortSpatial = true;

            vm.pointSpatial = false;

            vm.polygonSpatial = false;
        };

        function checkNoSpatialFilter() {

          // If we don't have any of the filters selected we will provide
          // a list of all the images.
          if (!vm.viewPortSpatial && !vm.pointSpatial && !vm.polygonSpatial) {

            console.log('checkNoSpatialFilter if !all: ');
            mapService.viewPortFilter(false);

          }

        }

        this.byViewPort = function(status) {

            // Turn on viewport
            mapService.viewPortFilter(status);

            // Turn off point
            vm.pointSpatial = false;
            mapService.pointFilter(vm.pointSpatial);

            // Turn off polygon
            vm.polygonSpatial = false;
            mapService.polygonFilter(vm.polygonSpatial);

            checkNoSpatialFilter();

        };

        this.byPointer = function(status) {

            // Turn on point
            mapService.pointFilter(status);

            // Turn off viewport
            vm.viewPortSpatial = false;
            mapService.viewPortFilter(vm.viewPortSpatial);

            // Turn off polygon
            vm.polygonSpatial = false;
            mapService.polygonFilter(vm.polygonSpatial);

            checkNoSpatialFilter();

        };

        this.byPolygon = function(status) {

            // Turn on polygons
            mapService.polygonFilter(status);

            // Turn off viewport
            vm.viewPortSpatial = false;
            mapService.viewPortFilter(vm.viewPortSpatial);

            // Turn off point
            vm.pointSpatial = false;
            mapService.pointFilter(vm.pointSpatial);

            checkNoSpatialFilter();

        };

        vm.initKeywords = function() {
            // Keywords
            vm.countryCodeCheck = false;
            vm.countryCode = "";

            vm.imageIdCheck = false;
            vm.imageId = "";

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

            // Clears out the current filter
            vm.updateFilterString();

        };

        vm.initRanges = function() {
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

            // Clears out the current filter
            vm.updateFilterString();

        };

        vm.dateTypes = [{
            value: 'acquisition_date',
            label: 'Acquisition Date'
        }, {
            value: 'ingest_date',
            label: 'Ingest Date'
        }];
        vm.currentDateType = vm.dateTypes[0]; // sets the first selected date type (acquisition_date)

        vm.temporalDurations = [{
            value: 'none',
            label: 'None'
        }, {
            value: 'lastDay',
            label: 'Today'
        }, {
            value: 'yesterday',
            label: 'Yesterday'
        }, {
            value: 'last3Days',
            label: 'Last 3 Days'
        }, {
            value: 'last7Days',
            label: 'Last Week'
        }, {
            value: 'lastMonth',
            label: 'Last Month'
        }, {
            value: 'last3Months',
            label: 'Last 3 Months'
        }, {
            value: 'last6Months',
            label: 'Last 6 Months'
        }, {
            value: 'customDateRange',
            label: 'Custom Date Range'
        }, ];
        vm.currentTemporalDuration = vm.temporalDurations[0];

        vm.customDateRangeVisible = false;

        vm.showCustomDateRange = function() {
            vm.customDateRangeVisible = true;
            console.log('vm.customDateRangeVisible', vm.customDateRangeVisible);
        };

        vm.setInitialCustomStartDate = function() {
            var yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);

            vm.startDate = yesterday;
        };

        vm.setInitialCustomEndDate = function() {
            vm.endDate = new Date();
            //vm.endDate = new Date(new Date().setMinutes(0, 0));
        };

        vm.getCustomStartDate = function() {
            console.log('Start: ' + vm.startDate);
            console.log('moment formatted start date', moment(vm.startDate).format('MM-DD-YYYY HH:mm:ss'));

            // TODO: Pickup the time from the timepicker control instead of formatting from moment
            console.log('vm.startDate before momnent', vm.startDate);
            return moment(vm.startDate).format('MM-DD-YYYY HH:mm:ss');
        };

        vm.getCustomEndDate = function() {
            //console.log('End: ' + vm.endDate);
            //console.log('moment formatted end date', moment(vm.endDate).format('MM-DD-YYYY'));

            // TODO: Pickup the time from the timepicker control instead of formatting from moment

            return moment(vm.endDate).format('MM-DD-YYYY HH:mm:ss');
        };

        vm.updateFilterString = function() {

            filterArray = [];

            // Move this to an init like the others?
            var dateToday = moment().format('MM-DD-YYYY 00:00');
            var dateTodayEnd = moment().format('MM-DD-YYYY 23:59');
            var dateYesterday = moment().subtract(1, 'days').format('MM-DD-YYYY 00:00');
            var dateYesterdayEnd = moment().subtract(1, 'days').format('MM-DD-YYYY 23:59');
            var dateLast3Days = moment().subtract(2, 'days').format('MM-DD-YYYY 00:00');
            var dateLast7Days = moment().subtract(7, 'days').format('MM-DD-YYYY 00:00');
            var dateThisMonth = moment().subtract(1, 'months').format('MM-DD-YYYY 00:00');
            var dateLast3Months = moment().subtract(3, 'months').format('MM-DD-YYYY 00:00');
            var dateLast6Months = moment().subtract(6, 'months').format('MM-DD-YYYY 00:00');

            var dbName = vm.currentDateType.value; //"acquisition_date";
            var temporalParam = vm.currentTemporalDuration.value;

            // Feed the switch statement from the value of the currently selected date range
            switch (temporalParam) {
                case "lastDay":
                    vm.customDateRangeVisible = false;
                    filterArray.push([dbName, ">='", dateToday, "'AND", dbName, "<='", dateTodayEnd, "'"].join(" "));
                    break;
                case "yesterday":
                    vm.customDateRangeVisible = false;
                    filterArray.push([dbName, ">='", dateYesterday, "'AND", dbName, "<='", dateYesterdayEnd, "'"].join(" "));
                    break;
                case "last3Days":
                    vm.customDateRangeVisible = false;
                    filterArray.push([dbName, ">='", dateLast3Days, "'AND", dbName, "<='", dateTodayEnd, "'"].join(" "));
                    break;
                case "last7Days":
                    vm.customDateRangeVisible = false;
                    filterArray.push([dbName, ">='", dateLast7Days, "'AND", dbName, "<='", dateTodayEnd, "'"].join(" "));
                    break;
                case "lastMonth":
                    vm.customDateRangeVisible = false;
                    filterArray.push([dbName, ">='", dateThisMonth, "'AND", dbName, "<='", dateTodayEnd, "'"].join(" "));
                    break;
                case "last3Months":
                    vm.customDateRangeVisible = false;
                    filterArray.push([dbName, ">='", dateLast3Months, "'AND", dbName, "<='", dateTodayEnd, "'"].join(" "));
                    break;
                case "last6Months":
                    vm.customDateRangeVisible = false;
                    filterArray.push([dbName, ">='", dateLast6Months, "'AND", dbName, "<='", dateTodayEnd, "'"].join(" "));
                    break;
                case "customDateRange":
                    //console.log('switch == "customDateRange"');
                    vm.customDateRangeVisible = true;
                    filterArray.push([dbName, ">='", vm.getCustomStartDate(), "'AND", dbName, "<='", vm.getCustomEndDate(), "'"].join(" "));
                    break;
                default:
                    vm.customDateRangeVisible = false;
                    //filterArray.push([dbName,  ">='", dateToday, "'AND", dbName, "<='",  dateTodayEnd, "'
                    // AND"].join(" "));
                    //filterArray.push([dbName,  ">='", dateToday, "'AND", dbName, "<='",  dateTodayEnd, "'
                    // AND"].join(" "));
                    //filterArray.push([" ",].join(" "));
                    //console.log('switch default working');
                    break;
            }

            function pushKeywordToArray(dbName, formField) {
                var clause = ["strToUpperCase(" + dbName + ") LIKE '%", formField.trim().toUpperCase(), "%'"].join("");

                var placemarksConfig = AppO2.APP_CONFIG.params.misc.placemarks;

                if (dbName === 'be_number' && placemarksConfig) {
                    var subClause = clause.replace('be_number', placemarksConfig.columnName);

                    subClause = subClause.split("'").join("''");

                    clause = "(" + clause + " or intersects(ground_geom, collectGeometries(queryCollection('" +
                        placemarksConfig.tableName + "', '" + placemarksConfig.geomName + "', '" + subClause + "'))))";

                    // alert( clause );
                }

                filterArray.push(clause);
                console.log(dbName + ' filterArray', filterArray);
            }

            function pushRangeToArray(dbName, formFieldMin, formFieldMax) {

                var min,
                    max;

                min = parseFloat(formFieldMin);
                max = parseFloat(formFieldMax);

                if (isNaN(min) || isNaN(max)) {
                    toastr.error('Please enter a valid number for the range filter.',
                        'Error', {
                            closeButton: true
                        });
                } else {
                    filterArray.push([dbName, ">=", min, "AND", dbName, "<=", max].join(" "));
                    console.log(dbName + 'filterArray', filterArray);
                }

            }

            // Keywords
            if (vm.beNumberCheck) {
                pushKeywordToArray("be_number", vm.beNumber);
            }
            if (vm.countryCodeCheck) {
                pushKeywordToArray("country_code", vm.countryCode);
            }
            if (vm.filenameCheck) {
                pushKeywordToArray("filename", vm.filename);
            }
            if (vm.imageIdCheck) {
                pushKeywordToArray("title", vm.imageId);
            }
            if (vm.missionIdCheck) {
                //filterArray.push(["mission_id Like '%", vm.missionId.trim() ,"%'"].join(""));
                pushKeywordToArray("mission_id", vm.missionId);
                //console.log('vm.missionIdCheck filterArray', filterArray);
            }
            if (vm.sensorIdCheck) {
                //filterArray.push(["sensor_id Like '%", vm.sensorId.trim(), "%'"].join(""));
                pushKeywordToArray("sensor_id", vm.sensorId);
                //console.log('vm.sensorIdCheck filterArray', filterArray);
            }
            if (vm.targetIdCheck) {
                //filterArray.push(["target_id Like '%", vm.targetId.trim(), "%'"].join(""));
                pushKeywordToArray("target_id", vm.targetId);
                //console.log('vm.target_id filterArray', filterArray);
            }
            if (vm.wacNumberCheck) {
                //filterArray.push(["wac_code Like '%", vm.wacNumber.trim(), "%'"].join(""));
                pushKeywordToArray("wac_code", vm.wacNumber);
                //console.log('vm.wac_code filterArray', filterArray);
            }

            // Ranges
            if (vm.predNiirsCheck) {

                //filterArray.push(["niirs",  ">=", vm.predNiirsMin, "AND", "niirs", "<=",
                //  vm.predNiirsMax].join(" "));
                pushRangeToArray("niirs", vm.predNiirsMin, vm.predNiirsMax);

            }
            if (vm.azimuthCheck) {

                //filterArray.push(["azimuth_angle",  ">=", vm.azimuthMin, "AND", "azimuth_angle", "<=",
                //  vm.azimuthMax].join(" "));
                pushRangeToArray("azimuth_angle", vm.azimuthMin, vm.azimuthMax);

            }
            if (vm.grazeElevCheck) {

                //filterArray.push(["grazing_angle",  ">=", vm.grazeElevMin, "AND", "grazing_angle", "<=",
                //  vm.grazeElevMax].join(" "));
                pushRangeToArray("grazing_angle", vm.grazeElevMin, vm.grazeElevMax);

            }
            if (vm.sunAzimuthCheck) {

                //filterArray.push(["sun_azimuth",  ">=", vm.sunAzimuthMin, "AND", "sun_azimuth", "<=",
                //  vm.sunAzimuthMax].join(" "));
                pushRangeToArray("sun_azimuth", vm.sunAzimuthMin, vm.sunAzimuthMax);

            }
            if (vm.sunElevationCheck) {

                //filterArray.push(["sun_elevation",  ">=", vm.sunElevationMin, "AND", "sun_elevation", "<=",
                //  vm.sunElevationMax].join(" "));
                pushRangeToArray("sun_elevation", vm.sunElevationMin, vm.sunElevationMax);

            }
            if (vm.cloudCoverCheck) {
                if (isNaN(vm.cloudCover)) {
                    toastr.error('Please enter a valid number for the range filter.',
                        'Error', {
                            closeButton: true
                        });
                } else {
                    filterArray.push(["cloud_cover", "<= " + vm.cloudCover].join(" "));
                }

            }

            filterString = filterArray.join(" AND ");
            //console.log('filterString', filterString);

            wfsService.updateAttrFilter(filterString);

        };

        vm.initSpatial();
        vm.initKeywords();
        vm.initRanges();

        vm.setInitialCustomStartDate();
        vm.setInitialCustomEndDate();

    }
})();
