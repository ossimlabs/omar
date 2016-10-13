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

    // set header title
    stateService.navState.titleLeft = "<h3>Search</h3>";
    stateService.navStateUpdate();

    var vm = this;
    var url;
    var twofishProxy = AppO2.APP_CONFIG.params.twofishes.proxy;
    var baseUrl = AppO2.APP_CONFIG.serverURL;
    var $el = $('#searchForm');
    var $searchInput = $el.find('#searchInput');

    vm.placeholder = 'Search O2 Map';

    // bind events
    $el.keypress(suppressKey);

    searchByPlace();

    // Remove enter/return key forcing a form
    // submit, and reloading the page
    function suppressKey(event) {

      if (event.keyCode === 10 || event.keyCode === 13) {

        event.preventDefault();

      }
    }

    // Searches twofishes service
    function searchByPlace() {

      //$searchInput.autocomplete('enable');

      url = baseUrl + twofishProxy + '/?responseIncludes=WKT_GEOMETRY_SIMPLIFIED' +
      '&autocomplete=true&maxInterpretations=10&autocompleteBias=BALANCED';

      $searchInput.autocomplete({
        serviceUrl: url,
        minChars: 3,
        dataType: 'json',
        type: 'GET',
        transformResult: function (response) {

            return {
              suggestions: $.map(response.interpretations, function (dataItem) {

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

          stateService.updateMapState(suggestion);

          }
      });

    }

    vm.resetSearchInput = function () {

      vm.searchInput = '';

    };

  }

}());
