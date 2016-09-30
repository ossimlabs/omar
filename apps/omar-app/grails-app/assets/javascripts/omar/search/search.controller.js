(function () {
  'use strict';
  angular
  .module('omarApp')
  .controller('SearchController', ['$scope', '$state', 'wfsService', '$http', 'stateService', SearchController]);

  function SearchController($scope, $state, wfsService, $http, stateService) {

    // #################################################################################
    // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
    // provides access to various client params in application.yml
    // #################################################################################
    //console.log('AppO2.APP_CONFIG in SearchController: ', AppO2.APP_CONFIG);

    var vm = this;
    var url;
    var twofishProxy = AppO2.APP_CONFIG.params.twofishes.proxy;

    vm.placeholder = 'Search O2 Map';
    vm.baseUrl = AppO2.APP_CONFIG.serverURL;

    // cache DOM
    var $el = $('#searchForm');
    var $searchInput = $el.find('#searchInput');

    // bind events
    $el.keypress(suppressKey);

    searchByPlace();

    /**
     * Remove enter/return key forcing a form
     * submit, and reloading the page
     * @function suppressKey
     * @memberof Search
     */
    function suppressKey(event) {
      if (event.keyCode === 10 || event.keyCode === 13) {
        event.preventDefault();
      }
    }

    /**
     * Clear the search input, and
     * remove the map marker and polygon boundaries
     * @function clearSearch
     * @memberof Search
     */
    // function clearSearch() {
    //   $searchInput.val('');
    //
    //   //Map.clearLayerSource(Map.searchLayerVector);
    //
    // }

    // function changeSearchType() {
    //
    //   console.log('changeSearchType firing!');
    //   var searchType = $searchSelect.val();
    //
    //   switch (searchType){
    //     case 'place':
    //       searchByPlace();
    //       console.log('place!');
    //       break;
    //     case 'coordinate':
    //       searchByCoordinates();
    //       console.log('coordinates!');
    //       break;
    //     default: console.log('nothing selected');
    //   }
    //
    //   return;
    // }

    /**
     * Searches the TwoFish geocoding engine using
     * a jquery autocomplete widget.  Pans and zooms
     * the map on a selected item.
     * @function searchByPlace
     * @memberof Search
     */
    function searchByPlace() {

      //$searchInput.val('');

      $searchInput.autocomplete('enable');

      url = vm.baseUrl + twofishProxy /*+ twofishUrl + twofishPort*/ + '/?responseIncludes=WKT_GEOMETRY_SIMPLIFIED&autocomplete=true&maxInterpretations=10&autocompleteBias=BALANCED';

      $searchInput.autocomplete({
        serviceUrl: url,
        minChars: 3,
        dataType: 'json',
        type: 'GET',
        transformResult: function (response) {
            //console.log('response', response);
            return {
              suggestions: $.map(response.interpretations, function (dataItem) {

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

            //console.log('You selected: ' + suggestion.value +
            //', \n' + suggestion.lat + ', \n' + suggestion.lng);
            //console.log('suggestion', suggestion);
            var sug;
            if (suggestion.bounds === undefined) {

              //console.log('bounds is undefined!');

              // Map.zoomTo(suggestion.lat, suggestion.lng);
              // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
              //sug = JSON.stringify(suggestion);

              //console.log('sug', sug);
              //$state.go('map', { mapParams: sug, maxFeatures: '10' });
              console.log('suggestion =>', suggestion);
              stateService.updateMapState(suggestion);

            } else {

              // Map.zoomToExt(suggestion);
              // Can not pass an object as a state paramenter - http://stackoverflow.com/a/26021346
              //sug = JSON.stringify(suggestion);

              //console.log('sug', sug);
              //$state.go('map', { mapParams: sug, maxFeatures: '10' });
              console.log('suggestion =>', suggestion);
              stateService.updateMapState(suggestion);
            }
          }
      });

    }

    vm.resetSearchInput = function() {

      vm.searchInput = "";

    }

  }

}());
