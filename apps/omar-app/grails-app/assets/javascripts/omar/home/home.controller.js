(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('HomeController', ['APP_CONFIG', '$state', 'toastr', HomeController]);

        function HomeController(APP_CONFIG, $state, toastr){

            toastr.info('This pre-alpha release has limitied functionality.  Some items' +
                ' are disabled at this time. More capabilities will be added on a continous' +
                ' basis.', 'Note:',{
                closeButton: true,
                timeOut: 10000,
                extendedTimeOut: 5000
            });

            /* jshint validthis: true */
            var vm = this;

            //var twofishUrl = APP_CONFIG.services.twofishes.url;
            //var twofishPort = APP_CONFIG.services.twofishes.port;
            var twofishProxy = APP_CONFIG.services.twofishes.proxy;

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

        }

})();
