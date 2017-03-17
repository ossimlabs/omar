(function () {
  //'use strict';
  angular
  .module('omarApp')
  .controller('SearchController', ['coordinateConversionService', '$scope', '$state', 'wfsService', '$http', 'stateService', 'toastr', SearchController]);

  function SearchController(coordinateConversionService, $scope, $state, wfsService, $http, stateService, toastr) {

    // #################################################################################
    // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
    // provides access to various client params in application.yml
    // #################################################################################

    // set header title
    stateService.navStateUpdate({
        titleLeft: "<h3>Search</h3>",
        userGuideUrl: "user-guide/search/"
    });

    var vm = this;

    var searchInput = $( '#searchInput' );
    searchInput.autocomplete({
        dataType: "json",
        minChars: 3,
        onSelect: function ( suggestion ) { vm.executeSearch(); },
        serviceUrl: AppO2.APP_CONFIG.serverURL + AppO2.APP_CONFIG.params.twofishes.proxy +
            "/?responseIncludes=WKT_GEOMETRY_SIMPLIFIED" +
            "&autocomplete=true&maxInterpretations=10&autocompleteBias=BALANCED",
        transformResult: function ( response ) {
            return formatTwoFishesResponse(response);
        },
        type: "GET"
    });
    searchInput.autocomplete('enable');

    searchInput.keypress(function( event ) {
        if( event.keyCode == 13 ) { // pressing Return or Enter
            vm.executeSearch();
        }
    });

    function formatTwoFishesResponse(response) {
        return {
            suggestions: $.map(response.interpretations, function (dataItem) {
                return {
                    bounds: dataItem.feature.geometry.bounds,
                    data: dataItem.feature.displayName,
                    lat: dataItem.feature.geometry.center.lat,
                    lng: dataItem.feature.geometry.center.lng,
                    value: dataItem.feature.displayName,
                    wkt: dataItem.feature.geometry.wktGeometrySimplified
                };
            })
        };
    }

    vm.executeSearch = function () {
        wfsService.updateAttrFilter('');

        var input = searchInput.val().trim();
        var wfsUrl = AppO2.APP_CONFIG.params.wfs.baseUrl +
            "filter=" + encodeURIComponent( "title LIKE '%" + input.toUpperCase() + "%'" ) +
            "&maxFeatures=100" +
            "&outputFormat=JSON" +
            "&request=GetFeature" +
            "&service=WFS" +
            "&typeName=omar:raster_entry" +
            "&version=1.1.0";

        $http({
            method: 'GET',
            url: wfsUrl
        }).then(function( response ) {
                var features = response.data.features;

                if ( features && features.length > 0 ) {
                    searchByImageId({
                        imageId: input,
                        images: features
                    });
                }
                else { coordinateConversionService.convert( input ); }
        });
    }

    vm.resetSearchInput = function () {
        vm.searchInput = '';
        wfsService.updateAttrFilter('');
    }

    $scope.$on('coordService: updated', function( event, response ) {
        if ( response ) {
            if ( response.bounds ) {
                stateService.updateMapState({
                    bounds: response.bounds,
                    lat: response.coordinate[1],
                    lng: response.coordinate[0]
                });
            }
            else {
                stateService.updateMapState({
                    lat: response.coordinate[1],
                    lng: response.coordinate[0]
                });
            }
        }
        else {
            toastr.error( "Sorry, we couldn't find anything for that location." );
        }
    });

    $scope.$on( 'coordService: be_search_error', function( event, message ) { toastr.error( message, 'Error' ); } );
    $scope.$on( 'coordService: twofishes_error', function( event, message ) { toastr.error( message, 'Error' ); } );

    function searchByImageId( imageObject ) {
        var geometries = [];
        $.each(
            imageObject.images,
            function( index, feature ) {
                var geometry = new ol.geom.MultiPolygon( feature.geometry.coordinates );
                geometries.push( geometry );
            }
        );
        var geometryCollection = new ol.geom.GeometryCollection( geometries );
        var bounds = geometryCollection.getExtent();
        var center = ol.extent.getCenter( bounds );
        var mapParams = {
            bounds: bounds,
            lat: center[1],
            lng: center[0]
        };
        stateService.updateMapState(mapParams);

        var filter = "title LIKE '%" + imageObject.imageId.toUpperCase() + "%'";
        wfsService.updateAttrFilter(filter);
    }
  }
}());
