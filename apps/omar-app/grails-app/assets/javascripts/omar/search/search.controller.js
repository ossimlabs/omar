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

    // Image ID variables
    var filterArray = [];
    var clause,
      filterString;

    // Place Name variables
    var url;
    var twofishProxy = AppO2.APP_CONFIG.params.twofishes.proxy;
    var baseUrl = AppO2.APP_CONFIG.serverURL;
    var $el = $('#searchForm');
    var $searchInput = $el.find('#searchInput');

    vm.placeholder = 'Search by Image ID';

    // bind events
    $el.keypress(suppressKey);

    // Remove enter/return key forcing a form
    // submit, and reloading the page
    function suppressKey(event) {

      if (event.keyCode === 10 || event.keyCode === 13) {

        event.preventDefault();

      }
    }

    vm.copyPastedImageId = function ($event) {

      // Reset the filter array
      filterArray = [];

      if (typeof $event.originalEvent.clipboardData !== 'undefined') {

        //vm.handlePastedData($event.originalEvent.clipboardData.getData('text/plain'));
        console.log('copyapi: ', $event.originalEvent.clipboardData.getData('text/plain'));
        pushKeywordToArray($event.originalEvent.clipboardData.getData('text/plain'));

        filterString = filterArray.join(" AND ");

        //filterString = filterArray;

        wfsService.updateAttrFilter(filterString);

      } else { // To support browsers without clipboard API (IE and older browsers)
        $timeout(function () {

          pushKeywordToArray(angular.element($event.currentTarget).val());

          filterString = filterArray.join(" AND ");

          wfsService.updateAttrFilter(filterString);

        });
      }

    };

    // Searches for Image ID
    function pushKeywordToArray(imageId) {

      clause = ["strToUpperCase(title) LIKE '%", imageId.trim().toUpperCase(), "%'"].join("");

      filterArray.push(clause);

    }

    // Searches twofishes service
    function searchByPlace() {

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

    vm.imageIdClass = 'btn btn-success';
    vm.coordinatesClass = 'btn btn-default';
    vm.placeClass = 'btn btn-default';

    vm.searchButtonDisabled = false;

    vm.byImageId = function () {

      //console.log('ng-click for byImageId...');
      vm.imageIdClass = 'btn btn-success';
      vm.coordinatesClass = 'btn btn-default';
      vm.placeClass = 'btn btn-default';

      vm.searchButtonDisabled = false;

      vm.placeholder = 'Search by Image ID';

      $searchInput.autocomplete('disable');

    };

    vm.byCoordinates = function () {

      //console.log('ng-click for byCoordinates');
      vm.imageIdClass = 'btn btn-default';
      vm.coordinatesClass = 'btn btn-success';
      vm.placeClass = 'btn btn-default';

      vm.searchButtonDisabled = false;

      vm.placeholder = 'Search by Coordinates';

      $searchInput.autocomplete('disable');

    }

    vm.byPlace = function () {

      //console.log('ng-click for byPlace');
      vm.imageIdClass = 'btn btn-default';
      vm.coordinatesClass = 'btn btn-default';
      vm.placeClass = 'btn btn-success';

      vm.searchButtonDisabled = true;

      vm.placeholder = 'Search by Place Name';

      $searchInput.autocomplete('enable');
      searchByPlace();

    };

    vm.executeSearch = function () {

      // Reset the filter array
      filterArray = [];

      pushKeywordToArray(vm.searchInput);

      filterString = filterArray.join(" AND ");

      wfsService.updateAttrFilter(filterString);

    };

    vm.resetSearchInput = function () {

      // Reset the filter array
      filterArray = [];
      vm.searchInput = '';
      wfsService.updateAttrFilter('');

    };

  }

}());
