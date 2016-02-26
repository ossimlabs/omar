(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('HomeController', [/*'APP_CONFIG', */'$scope', '$state', 'wfsService', 'toastr', '$http', HomeController]);

        function HomeController(/*APP_CONFIG,*/ $scope, $state, wfsService, toastr, $http, $timeout){

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

            /* jshint validthis: true */
            var vm = this;

            vm.loading = true;

            vm.swipeAppEnabled = AppO2.APP_CONFIG.clientParams.swipeApp.enabled;
            //console.log('Swipe enabled: ', vm.swipeAppEnabled);
            if (vm.swipeAppEnabled) {
                vm.swipeAppLink = AppO2.APP_CONFIG.clientParams.swipeApp.baseUrl;
                //console.log('vm.swipeAppLink in HomeController', vm.swipeAppLink);
            }

            vm.piwikAppEnabled = AppO2.APP_CONFIG.clientParams.piwikApp.enabled;
            //console.log('Piwik enabled: ', vm.piwikAppEnabled);
            if (vm.piwikAppEnabled) {
                vm.piwikAppLink = AppO2.APP_CONFIG.clientParams.piwikApp.baseUrl;
                //console.log('vm.piwikAppLink in HomeController', vm.swipeAppLink);
            }

            vm.apiAppEnabled = AppO2.APP_CONFIG.clientParams.apiApp.enabled;
            //console.log('API enabled: ', vm.apiAppEnabled);
            if (vm.apiAppEnabled) {
                vm.apiAppLink = AppO2.APP_CONFIG.clientParams.apiApp.baseUrl;
                //console.log('vm.apiAppLink in HomeController', vm.apiAppLink);
            }

            //var twofishProxy = APP_CONFIG.services.twofishes.proxy;
            var twofishProxy = AppO2.APP_CONFIG.clientParams.twofishes.proxy;
            console.log('twofisthProxy: ', twofishProxy);

            vm.title = 'Search for imagery';
            vm.titleMessage = 'Find by place name or coordinates';

            // initialize the selectpicker element
            $('.selectpicker').selectpicker('show');

            var url;

            // cache DOM
            var $el = $('#searchForm');
            var $searchSelect = $el.find('#searchSelect');
            var $searchInput = $el.find('#searchInput');
            var $searchButton = $el.find('#searchButton');
            var $clearSearchButton = $el.find('#clearSearchButton');

            // bind events
            $el.keypress(suppressKey);
            $searchSelect.on('change', changeSearchType);
            $clearSearchButton.on('click', clearSearch);

            searchByPlace();

            /**
             * Remove enter/return key forcing a form
             * submit, and reloading the page
             * @function suppressKey
             * @memberof Search
             */
            function suppressKey (event) {
                if (event.keyCode === 10 || event.keyCode === 13){
                    event.preventDefault();
                }
            }

            /**
             * Clear the search input, and
             * remove the map marker and polygon boundaries
             * @function clearSearch
             * @memberof Search
             */
            function clearSearch(){
                $searchInput.val('');
                //Map.clearLayerSource(Map.searchLayerVector);
            }

            function changeSearchType() {

                var searchType = $searchSelect.val();

                switch (searchType){
                    case 'place':
                        searchByPlace();
                        break;
                    case 'coordinate':
                        searchByCoordinates();
                        break;
                    default: console.log('nothing selected');
                }

                return 'changeSearchType fired';
            }

            /**
             * Searches the TwoFish geocoding engine using
             * a jquery autocomplete widget.  Pans and zooms
             * the map on a selected item.
             * @function searchByPlace
             * @memberof Search
             */
            function searchByPlace(){

                $searchInput.val('');
                $searchInput.attr("placeholder", "Search by place");
                $searchInput.autocomplete('enable');
                //$searchButton.off('click', ZoomTo.cycleRegExs);
                //console.log('place selected');
                url = twofishProxy /*+ twofishUrl + twofishPort*/ + '/?responseIncludes=WKT_GEOMETRY_SIMPLIFIED&autocomplete=true&maxInterpretations=10&autocompleteBias=BALANCED';

                $searchInput.autocomplete({
                    serviceUrl: url,
                    minChars: 3,
                    dataType: 'json',
                    type: 'GET',
                    transformResult: function(response) {
                        //console.log('response', response);
                        return {
                            suggestions: $.map(response.interpretations, function(dataItem){
                                //console.log(dataItem);
                                //console.log('value: ' + dataItem.feature.displayName + ' data: ' +
                                //dataItem.feature.displayName);
                                return {
                                    value: dataItem.feature.displayName,
                                    data: dataItem.feature.displayName,
                                    lat: dataItem.feature.geometry.center.lat,
                                    lng: dataItem.feature.geometry.center.lng,
                                    bounds: dataItem.feature.geometry.bounds,
                                    wkt: dataItem.feature.geometry.wktGeometrySimplified
                                };
                            })
                        };
                    },
                    onSelect: function (suggestion) {
                        //console.log('You selected: ' + suggestion.value + ', \n' + suggestion.lat + ', \n' + suggestion.lng);
                        //console.log('suggestion', suggestion);
                        var sug;
                        if (suggestion.bounds === undefined){
                            console.log('bounds is undefined!');
                            //Map.zoomTo(suggestion.lat, suggestion.lng);
                            // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
                            sug = JSON.stringify(suggestion);
                            //console.log('sug', sug);
                            $state.go('map', {mapParams: sug, maxFeatures: '10'});
                        }
                        else {
                            //Map.zoomToExt(suggestion);
                            // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
                            sug = JSON.stringify(suggestion);
                            //console.log('sug', sug);
                            $state.go('map', {mapParams: sug, maxFeatures: '10'});
                        }

                    }
                });

            }

            function searchByCoordinates(){
                $searchInput.val('');
                $searchInput.attr("placeholder", "Search by coordinate");
                $searchInput.autocomplete('disable');
                //$searchButton.on('click', ZoomTo.cycleRegExs);
            }
            
            function pioExecute() {

               // TODO: Follow up on moving this to a PIO service...
                vm.getTrendingPio = function(){

                    //console.log('showPopularItems firing...');
                    var pioUrl = AppO2.APP_CONFIG.clientParams.predio.baseUrl + 'getPopularItems';
                    $http({
                        method: 'GET',
                        url: pioUrl
                    })
                    .then(function(response) {
                        var data;
                        data = response;  // callback response from Predictive IO service
                        //console.log(data);
                        //formatTrendingList(data);
                        wfsService.executeWfsTrendingThumbs(data);
                    });

                };
                vm.getTrendingPio(); // get the top 10 trending images on page load and throw them into the carousel

                vm.trendingImages = {};

                $scope.$on('wfsTrendingThumb: updated', function(event, data) {

                    $scope.$apply(function(){

                        vm.trendingImages = data;
                        console.log('vm.trendingImages: ', vm.trendingImages);
                        vm.loading = false;

                    });

                }); 

            }

            vm.pioAppEnabled = AppO2.APP_CONFIG.clientParams.predio.enabled;
            console.log('PIO enabled: ', vm.pioAppEnabled);
            if (vm.pioAppEnabled) {
                console.log('pioAppEnabled: ', vm.pioAppEnabled);
                pioExecute();
            }

            // // TODO: Follow up on moving this to a PIO service...
            // vm.getTrendingPio = function(){

            //     //console.log('showPopularItems firing...');
            //     var pioUrl = AppO2.APP_CONFIG.clientParams.predio.baseUrl + 'getPopularItems';
            //     $http({
            //         method: 'GET',
            //         url: pioUrl
            //     })
            //     .then(function(response) {
            //         var data;
            //         data = response;  // callback response from Predictive IO service
            //         //console.log(data);
            //         //formatTrendingList(data);
            //         wfsService.executeWfsTrendingThumbs(data);
            //     });

            // };
            // vm.getTrendingPio(); // get the top 10 trending images on page load and throw them into the carousel

            // vm.trendingImages = {};

            // $scope.$on('wfsTrendingThumb: updated', function(event, data) {

            //     $scope.$apply(function(){

            //         vm.trendingImages = data;
            //         console.log('vm.trendingImages: ', vm.trendingImages);
            //         vm.loading = false;

            //     });

            // });

            //function formatTrendingList(trendData) {
            //
            //    var wfsImagesList = [];
            //    trendData.data.itemScores.filter(function(el){
            //
            //        console.log(el);
            //        wfsImagesList.push(el.item);
            //
            //    });
            //
            //
            //    var wfsImageString = wfsImagesList.join(",");
            //
            //    // TODO: Move this $http to the wfs.service.js
            //    var wfsRequest = {
            //        typeName: 'omar:raster_entry',
            //        namespace: 'http://omar.ossim.org',
            //        version: '1.1.0',
            //        outputFormat: 'JSON',
            //        cql: '',
            //    };
            //
            //    wfsRequest.cql = 'id in(' + wfsImageString + ')';
            //
            //    var wfsRequestUrl = APP_CONFIG.services.omar.wfsUrl + "?";
            //
            //    var wfsUrl = wfsRequestUrl +
            //        "service=WFS" +
            //        "&version=" + wfsRequest.version +
            //        "&request=GetFeature" +
            //        "&typeName=" + wfsRequest.typeName +
            //        "&filter=" + wfsRequest.cql +
            //        "&outputFormat=" + wfsRequest.outputFormat;
            //
            //    var url = encodeURI(wfsUrl);
            //
            //    $http({
            //        method: 'GET',
            //        url: url
            //    })
            //    .then(function(response) {
            //        var data;
            //        data = response.data.features;
            //        console.log('data from wfs', data);
            //        vm.trendingImages = data;
            //    });
            //
            //}


            vm.imageClick = function(imageId){
                console.log('imageClick imageId: ', imageId);
                $state.go('mapOrtho', {layers: imageId});
            };

        }

})();
