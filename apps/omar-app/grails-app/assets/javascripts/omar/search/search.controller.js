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

    // set header title
    stateService.navStateUpdate({ titleLeft: "<h3>Search</h3>" });

    var vm = this;

    var searchParams = {};
    var searchInput = $('#searchInput');

    // enable twofishes service
    var baseUrl = AppO2.APP_CONFIG.serverURL;
    var twoFishesProxy = AppO2.APP_CONFIG.params.twofishes.proxy;
    var twoFishesUrl = baseUrl + twoFishesProxy + "/?responseIncludes=WKT_GEOMETRY_SIMPLIFIED" +
        "&autocomplete=true&maxInterpretations=10&autocompleteBias=BALANCED";

    searchInput.autocomplete({
        dataType: "json",
        minChars: 3,
        onSelect: function ( suggestion ) { vm.executeSearch(); },
        serviceUrl: twoFishesUrl,
        transformResult: function ( response ) {
            return formatTwoFishesResponse(response);
        },
        type: "GET"
    });
    searchInput.autocomplete('enable');

    function determineGeospatialInput() {
        if (typeof searchParams.isCoordinate == "undefined") {
            searchParams.isCoordinate = isCoordinate();
            return;
        }
        else if (searchParams.isCoordinate != null) {
            searchByCoordinates(searchParams.isCoordinate);
            return;
        }

        if (typeof searchParams.isBeNumber == "undefined") {
            searchParams.isBeNumber = isBeNumber();
            return;
        }
        else if (searchParams.isBeNumber != null) {
            searchByCoordinates(searchParams.isBeNumber);
            return;
        }

        if (typeof searchParams.isImageId == "undefined") {
            searchParams.isImageId = isImageId();
            return;
        }
        else if (searchParams.isImageId != null) {
            searchByImageId(searchParams.isImageId);
            return;
        }

        if (typeof searchParams.isPlacename == "undefined") {
            searchParams.Placename = isPlacename();
            return;
        }
        else if (searchParams.isPlacename != null) {
            searchByPlacename(searchParams.isPlacename);
            return;
        }

        toastr.error("Sorry, we couldn't find anything that matched your input. :(");
    }

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

    function isBeNumber() {
        var beLookupEnabled = AppO2.APP_CONFIG.params.misc.beLookupEnabled;
        if (beLookupEnabled) {
            var columnName = AppO2.APP_CONFIG.params.misc.placemarks.columnName;
            var tableName = AppO2.APP_CONFIG.params.misc.placemarks.tableName;

            /* This pattern will have to changed in the C2S deployment */
            var bePattern = /(.{10})/;
            var beNumber = searchInput.val().trim();
            if (beNumber.match(bePattern)) {


                var wfsUrl = AppO2.APP_CONFIG.params.wfs.baseUrl +
                    "filter=" + columnName + " LIKE '" + beNumber + "'" +
                    "&maxFeatures=1" +
                    "&outputFormat=JSON" +
                    "&request=GetFeature" +
                    "&service=WFS" +
                    "&typeName=" + tableName +
                    "&version=1.1.0";
                $http({
                    method: 'GET',
                    url: encodeURI(wfsUrl)
                }).then(function(response) {
                        var features = response.data.features;
                        if (features.length > 0) {
                            searchParams.isBeNumber = features[0].geometry.coordinates;
                        }
                        else { searchParams.isBeNumber = null; }

                        determineGeospatialInput();
                });
            }
        }
        else { searchParams.isBeNumber = null; }
    }

    function isCoordinate() {
        var ddPattern = /(\-?\d{1,2}[.]?\d*)[\s+|,?]\s*(\-?\d{1,3}[.]?\d*)/;
        var dmsPattern = /(\d{2})[^\d]*(\d{2})[^\d]*(\d{2}[.]?\d*)([n|N|s|S])[^\w]*(\d{3})[^\d]*(\d{2})[^d]*(\d{2}[.]?\d*)([e|E|w|W])/;
        var mgrsPattern = /(\d{1,2})([a-zA-Z])[^\w]*([a-zA-Z])([a-zA-Z])[^\w]*(\d{5})[^\w]*(\d{5})/;
        var coordinate = searchInput.val().trim();
        var latitude, longitude = null;
        // dms must be first
        if (coordinate.match(dmsPattern)) {
            function dmsToDd (degrees, minutes, seconds, position) {
                var dd = Math.abs(degrees) + Math.abs(minutes / 60) + Math.abs(seconds / 3600);
                if (position.toUpperCase() == "S" || position.toUpperCase() == "W") { dd = -dd; }


                return dd;
            }

            latitude = dmsToDd(RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4);
            longitude = dmsToDd(RegExp.$5, RegExp.$6, RegExp.$7, RegExp.$8);
        }
        else if (coordinate.match(ddPattern)) {
            latitude = parseFloat(RegExp.$1);
            longitude = parseFloat(RegExp.$2);
        }
        else if (coordinate.match(mgrsPattern)) {
            var mgrsString = RegExp.$1 + RegExp.$2 + RegExp.$3 + RegExp.$4 + RegExp.$5 + RegExp.$6;
            var coords = mgrs.toPoint(mgrsString);

            latitude = coords[1];
            longitude = coords[0];
        }

        if ((latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180)) {
            searchParams.isCoordinate = [longitude, latitude];
        }
        else { searchParams.isCoordinate = null; }


        determineGeospatialInput();
    }

    function isImageId() {
        var imageId = searchInput.val().trim();
        var wfsUrl = AppO2.APP_CONFIG.params.wfs.baseUrl +
            "filter=title LIKE '%" + imageId.toUpperCase() + "%'" +
            "&maxFeatures=100" +
            "&outputFormat=JSON" +
            "&request=GetFeature" +
            "&service=WFS" +
            "&typeName=omar:raster_entry" +
            "&version=1.1.0";
        $http({
            method: 'GET',
            url: encodeURI(wfsUrl)
        }).then(function(response) {
                var features = response.data.features;
                if (features.length > 0) {
                    searchParams.isImageId = {
                        imageId: imageId,
                        images: features
                    };
                }
                else { searchParams.isImageId = null; }

                determineGeospatialInput();
        });
    }

    function isPlacename() {
        var placename = searchInput.val().trim();
        var placenameUrl = twoFishesUrl + "&query=" + placename;
        $http({
            method: 'GET',
            url: encodeURI(placenameUrl)
        }).then(function(response) {
            var suggestions = formatTwoFishesResponse(response.data).suggestions;
            if (suggestions.length > 0) {
                searchParams.isPlacename = suggestions[0];
            }
            else { searchParams.isPlacename = null; }

            determineGeospatialInput();
        });
    }

    function searchByCoordinates(coordinates) {
        var mapParams = {
            lat: coordinates[1],
            lng: coordinates[0]
        };
        stateService.updateMapState(mapParams);
    }

    function searchByImageId(imageObject) {
        var geometries = [];
        $.each(
            imageObject.images,
            function(i, x) {
                var geometry = new ol.geom.MultiPolygon(x.geometry.coordinates);
                geometries.push(geometry);
            }
        );
        var geometryCollection = new ol.geom.GeometryCollection(geometries);
        var bounds = geometryCollection.getExtent();
        var center = ol.extent.getCenter(bounds);
        var mapParams = {
            bounds: bounds,
            lat: center[1],
            lng: center[0]
        };
        stateService.updateMapState(mapParams);

        var filter = "title LIKE '%" + imageObject.imageId.toUpperCase() + "%'";
        wfsService.updateAttrFilter(filter);
    }

    function searchByPlacename(suggestion) {
        stateService.updateMapState(suggestion);
    }

    vm.executeSearch = function () {
        searchParams = {};
        wfsService.updateAttrFilter('');
        determineGeospatialInput();
    };

    vm.resetSearchInput = function () {
      vm.searchInput = '';
      wfsService.updateAttrFilter('');
    };
  }
}());
