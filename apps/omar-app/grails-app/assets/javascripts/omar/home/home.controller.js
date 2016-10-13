(function () {
  'use strict';
  angular
    .module('omarApp')
    .controller('HomeController', [/*'APP_CONFIG', */'$scope', '$state', 'stateService', 'wfsService', 'toastr', '$http', HomeController]);

        function HomeController(/*APP_CONFIG,*/ $scope, $state, stateService, wfsService, toastr, $http, $timeout){

            // #################################################################################
            // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
            // provides access to various client params in application.yml
            // #################################################################################
            //console.log('AppO2.APP_CONFIG in HomeController: ', AppO2.APP_CONFIG);

            // toastr.info('This pre-alpha release has limited functionality.  Some items' +
            //     ' are disabled at this time. More capabilities will be added on a continous' +
            //     ' basis.', 'Note:',{
            //     closeButton: true,
            //     timeOut: 10000,
            //     extendedTimeOut: 5000
            // });

            // set header title
            stateService.navState.titleLeft = "<h3>Welcome!</h3>";
            stateService.navStateUpdate();

            /* jshint validthis: true */
            var vm = this;

            vm.loading = true;

            vm.baseUrl = AppO2.APP_CONFIG.serverURL;
            //console.log('AppO2.APP_CONFIG.params: ', AppO2.APP_CONFIG.params)

            vm.swipeAppEnabled = AppO2.APP_CONFIG.params.swipeApp.enabled;
            //console.log('Swipe enabled: ', vm.swipeAppEnabled);
            if (vm.swipeAppEnabled) {
                vm.swipeAppLink = AppO2.APP_CONFIG.params.swipeApp.baseUrl;
                //console.log('vm.swipeAppLink in HomeController', vm.swipeAppLink);
            }

            vm.piwikAppEnabled = AppO2.APP_CONFIG.params.piwikApp.enabled;
            //console.log('Piwik enabled: ', vm.piwikAppEnabled);
            if (vm.piwikAppEnabled) {
                vm.piwikAppLink = AppO2.APP_CONFIG.params.piwikApp.baseUrl;
                //console.log('vm.piwikAppLink in HomeController', vm.swipeAppLink);
            }

            vm.apiAppEnabled = AppO2.APP_CONFIG.params.apiApp.enabled;
            //console.log('API enabled: ', vm.apiAppEnabled);
            if (vm.apiAppEnabled) {
                vm.apiAppLink = AppO2.APP_CONFIG.params.apiApp.baseUrl;
                //console.log('vm.apiAppLink in HomeController', vm.apiAppLink);
            }

            vm.wmtsAppEnabled = AppO2.APP_CONFIG.params.wmtsApp.enabled;
            //console.log('WMTS enabled: ', vm.wmtsAppEnabled);
            if (vm.wmtsAppEnabled) {
                vm.wmtsAppLink = AppO2.APP_CONFIG.params.wmtsApp.baseUrl;
              //console.log('vm.wmtsAppLink in HomeController', vm.wmtsAppLink);
            }

            //var twofishProxy = APP_CONFIG.services.twofishes.proxy;
            var twofishProxy = AppO2.APP_CONFIG.params.twofishes.proxy;
            //console.log('twofisthProxy: ', twofishProxy);

            vm.title = 'Image Discovery and Analysis';
            vm.titleMessage = 'Find by place name or coordinates';

            // // initialize the selectpicker element
            // $('.selectpicker').selectpicker('show');
            //
            // var url;
            //
            // // cache DOM
            // var $el = $('#searchForm');
            // var $searchSelect = $el.find('#searchSelect');
            // var $searchInput = $el.find('#searchInput');
            // var $searchButton = $el.find('#searchButton');
            // var $clearSearchButton = $el.find('#clearSearchButton');
            //
            // // bind events
            // $el.keypress(suppressKey);
            // $searchSelect.on('change', changeSearchType);
            // $clearSearchButton.on('click', clearSearch);
            //
            // searchByPlace();
            //
            // /**
            //  * Remove enter/return key forcing a form
            //  * submit, and reloading the page
            //  * @function suppressKey
            //  * @memberof Search
            //  */
            // function suppressKey (event) {
            //     if (event.keyCode === 10 || event.keyCode === 13){
            //         event.preventDefault();
            //     }
            // }
            //
            // /**
            //  * Clear the search input, and
            //  * remove the map marker and polygon boundaries
            //  * @function clearSearch
            //  * @memberof Search
            //  */
            // function clearSearch(){
            //     $searchInput.val('');
            //     //Map.clearLayerSource(Map.searchLayerVector);
            // }
            //
            // function changeSearchType() {
            //
            //     console.log('changeSearchType firing!');
            //     var searchType = $searchSelect.val();
            //
            //     switch (searchType){
            //         case 'place':
            //             searchByPlace();
            //             console.log('place!');
            //             break;
            //         case 'coordinate':
            //             searchByCoordinates();
            //             console.log('coordinates!');
            //             break;
            //         default: console.log('nothing selected');
            //     }
            //
            //     return 'changeSearchType fired';
            // }
            //
            // /**
            //  * Searches the TwoFish geocoding engine using
            //  * a jquery autocomplete widget.  Pans and zooms
            //  * the map on a selected item.
            //  * @function searchByPlace
            //  * @memberof Search
            //  */
            // function searchByPlace(){
            //
            //     $searchInput.val('');
            //     $searchInput.attr("placeholder", "Search by place");
            //     $searchInput.autocomplete('enable');
            //     //$searchButton.off('click', ZoomTo.cycleRegExs);
            //     //console.log('place selected');
            //
            //     url = vm.baseUrl + twofishProxy /*+ twofishUrl + twofishPort*/ + '/?responseIncludes=WKT_GEOMETRY_SIMPLIFIED&autocomplete=true&maxInterpretations=10&autocompleteBias=BALANCED';
            //
            //     $searchInput.autocomplete({
            //         serviceUrl: url,
            //         minChars: 3,
            //         dataType: 'json',
            //         type: 'GET',
            //         transformResult: function(response) {
            //             //console.log('response', response);
            //             return {
            //                 suggestions: $.map(response.interpretations, function(dataItem){
            //                     //console.log(dataItem);
            //                     //console.log('value: ' + dataItem.feature.displayName + ' data: ' +
            //                     //dataItem.feature.displayName);
            //                     return {
            //                         value: dataItem.feature.displayName,
            //                         data: dataItem.feature.displayName,
            //                         lat: dataItem.feature.geometry.center.lat,
            //                         lng: dataItem.feature.geometry.center.lng,
            //                         bounds: dataItem.feature.geometry.bounds,
            //                         wkt: dataItem.feature.geometry.wktGeometrySimplified
            //                     };
            //                 })
            //             };
            //         },
            //         onSelect: function (suggestion) {
            //             //console.log('You selected: ' + suggestion.value + ', \n' + suggestion.lat + ', \n' + suggestion.lng);
            //             //console.log('suggestion', suggestion);
            //             var sug;
            //             if (suggestion.bounds === undefined){
            //                 console.log('bounds is undefined!');
            //                 //Map.zoomTo(suggestion.lat, suggestion.lng);
            //                 // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
            //                 sug = JSON.stringify(suggestion);
            //                 //console.log('sug', sug);
            //                 $state.go('map', {mapParams: sug, maxFeatures: '10'});
            //             }
            //             else {
            //                 //Map.zoomToExt(suggestion);
            //                 // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
            //                 sug = JSON.stringify(suggestion);
            //                 //console.log('sug', sug);
            //                 $state.go('map', {mapParams: sug, maxFeatures: '10'});
            //             }
            //
            //         }
            //     });
            //
            // }

            function searchByCoordinates(){
                $searchInput.val('');
                $searchInput.attr("placeholder", "Search by coordinate");
                $searchInput.autocomplete('disable');
                //$searchButton.on('click', cycleRegExs());

                //TODO: need to change state and pass the lat/lon
            }

        // ################################
        // Regular expression for the input types
        var dRegExp = /^\s*(\-?\d{1,2})\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3})\s*\u00B0?\s*([WwEe])?\s*$/;
        var ddRegExp = /^\s*(\-?\d{1,2}\.\d*)\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3}\.\d*)\s*\u00B0?\s*([WwEe])?\s*$/;
        var dmsRegExp = /^\s*(\d{1,2})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([NnSs])\s*(\d{1,3})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([EeWw])\s*$/;
        var mgrsRegExp = /^\s*(\d{1,2})\s*([A-Za-z])\s*([A-Za-z])\s*([A-Za-z])\s*(\d{1,5})\s*(\d{1,5})\s*$/;

        function getNum(val) {
            if (typeof val === 'undefined'){
                return "";
            }
            else if (isNaN(val)){
                return "";
            }
            return val;
        }
        var lat,
            lon,
            coordinatesString;
        var coordinates = {};

        this.cycleRegExs = function() {

            var searchInput = $searchInput.val();
            searchInput.trim();

            if (searchInput.match(ddRegExp)) {

                //console.log(coordInput.match(ddRegExp));
                //console.log('0= ' + coordInput.match(ddRegExp)[0]);
                //console.log('1= ' + coordInput.match(ddRegExp)[1]);
                //console.log('2= ' + coordInput.match(ddRegExp)[2]);
                //console.log('3= ' + coordInput.match(ddRegExp)[3]);

                var latNum = searchInput.match(ddRegExp)[1];
                var latDir = searchInput.match(ddRegExp)[2];

                var lonNum = searchInput.match(ddRegExp)[3];
                var lonDir = searchInput.match(ddRegExp)[4];

                if ((latNum >= -90 && latNum <= 90) && (lonNum >= -180 && lonNum <= 180)) {

                    // check if lat is north or south
                    if(latDir === "S" || latDir === "s") {
                        lat = -latNum;
                    }
                    else {
                        lat = latNum;
                    }

                    // check if lon is east or west
                    if(lonDir === "W" || lonDir === "w") {
                        lon = -lonNum;
                    }
                    else {
                        lon = lonNum;
                    }

                    //zoomTo(lat, lon);
                    coordinates.lat = lat;
                    coordinates.lng = lon;
                    coordinatesString = JSON.stringify(coordinates);

                    $state.go('map', {mapParams: coordinatesString});

                }
                else {
                    toastr.error('Sorry, could not locate coordinates: [' + $coordInput.val() + '] Please check the' +
                        ' formatting' +
                        ' and' +
                        ' try' +
                        ' again.', 'No Match');
                }

                console.log('DD Match');
                console.log('input: ' + searchInput);
                console.log('result: ' + lat + " " + lon);
            }

            else if (searchInput.match(dRegExp)) {

                //console.log(searchInput.match(ddRegExp));
                //console.log('0= ' + coordInput.match(ddRegExp)[0]);
                //console.log('1= ' + coordInput.match(ddRegExp)[1]);
                //console.log('2= ' + coordInput.match(ddRegExp)[2]);
                //console.log('3= ' + coordInput.match(ddRegExp)[3]);

                latNum = searchInput.match(dRegExp)[1];
                latDir = searchInput.match(dRegExp)[2];

                lonNum = searchInput.match(dRegExp)[3];
                lonDir = searchInput.match(dRegExp)[4];

                if ((latNum >= -90 && latNum <= 90) && (lonNum >= -180 && lonNum <= 180)) {

                    // check if lat is north or south
                    if(latDir === "S" || latDir === "s") {
                        lat = -latNum;
                    }
                    else {
                        lat = latNum;
                    }

                    // check if lon is east or west
                    if(lonDir === "W" || lonDir === "w") {
                        lon = -lonNum;
                    }
                    else {
                        lon = lonNum;
                    }

                    zoomTo(lat, lon);
                }
                else {
                    toastr.error('Sorry, could not locate coordinates: [' + $searchInput.val() + '] Please check the' +
                        ' formatting' +
                        ' and' +
                        ' try' +
                        ' again.', 'No Match');
                }

                console.log('D Match');
                console.log('input: ' + searchInput);
                console.log('result: ' + lat + " " + lon);
            }

            else if (searchInput.match(dmsRegExp)) {

                //console.log(coordInput.match(dmsRegExp));
                //console.log('0= ' + coordInput.match(dmsRegExp)[0]);
                //console.log('1= ' + coordInput.match(dmsRegExp)[1]);
                //console.log('2= ' + coordInput.match(dmsRegExp)[2]);
                //console.log('3= ' + coordInput.match(dmsRegExp)[3]);
                //console.log('4= ' + coordInput.match(dmsRegExp)[4]);
                //console.log('5= ' + coordInput.match(dmsRegExp)[5]);
                //console.log('6= ' + coordInput.match(dmsRegExp)[6]);
                //console.log('7= ' + coordInput.match(dmsRegExp)[7]);
                //console.log('8= ' + coordInput.match(dmsRegExp)[8]);
                //console.log('9= ' + coordInput.match(dmsRegExp)[9]);
                //console.log('10= ' + coordInput.match(dmsRegExp)[10]);

                //var dms = coordInput.match(dmsRegExp)[0];

                var latDeg = searchInput.match(dmsRegExp)[1]; // degrees
                var latMin = searchInput.match(dmsRegExp)[2]; // minutes
                var latSec = (searchInput.match(dmsRegExp)[3]) + getNum(searchInput.match(dmsRegExp)[4]); // seconds decimal
                // number
                var latHem = searchInput.match(dmsRegExp)[5]; // hemisphere

                var lonDeg = searchInput.match(dmsRegExp)[6]; // degrees
                var lonMin = searchInput.match(dmsRegExp)[7]; // minutes
                var lonSec = (searchInput.match(dmsRegExp)[8]) + getNum(searchInput.match(dmsRegExp)[9]); // seconds
                // decimal number
                var lonHem = searchInput.match(dmsRegExp)[10]; // hemisphere

                if ((latDeg >= -90 && latDeg <= 90) && (lonDeg >= -180 && lonDeg <= 180)) {

                    lat = dmsToDd(latDeg, latMin, latSec, latHem);
                    lon = dmsToDd(lonDeg, lonMin, lonSec, lonHem);
                    //zoomTo(lat, lon);
                    coordinates.lat = lat;
                    coordinates.lng = lon;
                    coordinatesString = JSON.stringify(coordinates);

                    $state.go('map', {mapParams: coordinatesString});

                }
                else {
                    toastr.error('Sorry, could not locate coordinates: [' + $coordInput.val() + '] Please check the' +
                        ' formatting' +
                        ' and' +
                        ' try' +
                        ' again.', 'No Match');
                }

                console.log('DMS Match');
                console.log('input: ' + searchInput);
                console.log('result: ' + lat + " " + lon);
            }

            else if (searchInput.match(mgrsRegExp)) {

                //var mgrsAll = coordInput.match(mgrsRegExp);
                //var mgrs0 = coordInput.match(mgrsRegExp)[0];
                var mgrs1 = searchInput.match(mgrsRegExp)[1];
                var mgrs2 = searchInput.match(mgrsRegExp)[2];
                var mgrs3 = searchInput.match(mgrsRegExp)[3];
                var mgrs4 = searchInput.match(mgrsRegExp)[4];
                var mgrs5 = searchInput.match(mgrsRegExp)[5];
                var mgrs6 = searchInput.match(mgrsRegExp)[6];

                //console.log('mgrsAll: ' + mgrsAll);
                //console.log('mgrs0: ' + mgrs0);
                //console.log('mgrs1: ' + mgrs1);
                //console.log('mgrs2: ' + mgrs2);
                //console.log('mgrs3: ' + mgrs3);
                //console.log('mgrs4: ' + mgrs4);
                //console.log('mgrs5: ' + mgrs5);
                //console.log('mgrs6: ' + mgrs6);

                // Using mgrs.js toPoint, and then using the zoomTo (set at zoom level 12):
                var mgrsPoint = mgrs.toPoint(mgrs1+mgrs2+mgrs3+mgrs4+mgrs5+mgrs6);
                console.log('------------<mgrsPoint>-----------');
                console.log(mgrsPoint);
                console.log('------------</mgrsPoint>----------');
                //zoomTo(mgrsPoint[1], mgrsPoint[0]);

                coordinates.lat = mgrsPoint[1];
                coordinates.lng = mgrsPoint[0];
                coordinatesString = JSON.stringify(coordinates);

                $state.go('map', {mapParams: coordinatesString});

                // ####################################    WIP   #####################################################
                // mgrs.inverse uses the mgrs.js library to return a bounding box.  I am leaving this code here in
                // case we want to have the input mgrs location zoom to the appropriate location on the mgris grid.
                // At this time, if a user that inputs: 33UXP0500444998 it would create a 1m bounding box, and zoom
                // the map to the extent of the bounding box.  We would need to offset the extent by a given factor
                // so that it would not require the user to zoom bout 4-6 times to get to an acceptable level.
                //var bBox = mgrs.inverse(mgrs1+mgrs2+mgrs3+mgrs4+mgrs5+mgrs6);
                //var bBox = mgrs.inverse($coordInput.val());
                //console.log('------------<bBox>-----------');
                //console.log(bBox);
                //console.log('------------</bBox>----------');
                //
                //var mgrsExtent = bBox //[minlon, minlat, maxlon, maxlat];
                //mgrsExtent = ol.extent.applyTransform(mgrsExtent, ol.proj.getTransform("EPSG:4326", "EPSG:3857"));

                //map.getView().fitExtent(mgrsExtent, map.getSize());

                // ####################################    /WIP   ####################################################
            }

            else {
                console.log('No Match');
                toastr.error('Sorry, could not locate coordinates: [' + $searchInput.val() + '] Please check the' +
                    ' formatting' +
                    ' and' +
                    ' try' +
                    ' again.', 'No Match');
           }
        }

        function dmsToDd (degrees, minutes, seconds, position) {

            var dd = Math.abs(degrees) + Math.abs(minutes / 60) + Math.abs(seconds / 3600);

            if (position == "S" || position == "s" || position == "W" || position == "w") {
                dd = -dd;
            }

            return dd;
        }

  }

})();
