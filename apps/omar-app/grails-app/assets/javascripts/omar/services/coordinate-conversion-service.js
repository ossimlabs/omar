(function() {
    'use strict';
    angular
        .module( 'omarApp' )
        .service( 'coordinateConversionService', [ '$http', '$rootScope', coordinateConversionService ] );

        function coordinateConversionService( $http, $rootScope) {

            this.convert = function( location ) {
                var bePattern = /(.{10})/;
                var ddPattern = /(\-?\d{1,2}[.]?\d*)[\s+|,?]\s*(\-?\d{1,3}[.]?\d*)/;
                var dmsPattern = /(\d{2})[^\d]*(\d{2})[^\d]*(\d{2}[.]?\d*)\s*([n|N|s|S])[^\w]*(\d{3})[^\d]*(\d{2})[^d]*(\d{2}[.]?\d*)\s*([e|E|w|W])/;
                var mgrsPattern = /(\d{1,2})([a-zA-Z])[^\w]*([a-zA-Z])([a-zA-Z])[^\w]*(\d{5})[^\w]*(\d{5})/;

                // dms must be first
                if ( location.match( dmsPattern ) ) {
                    function dmsToDd( degrees, minutes, seconds, position ) {
                        var dd = Math.abs( degrees ) + Math.abs( minutes / 60 ) + Math.abs( seconds / 3600 );
                        if ( position.toUpperCase() == "S" || position.toUpperCase() == "W" ) { dd = -dd; }

                        return dd;
                    }

                    var latitude = dmsToDd( RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4 );
                    var longitude = dmsToDd( RegExp.$5, RegExp.$6, RegExp.$7, RegExp.$8 );

                    $rootScope.$broadcast('coordService: updated', { coordinate: [ longitude, latitude ] } );

                }
                else if ( location.match( ddPattern ) ) {
                    var latitude = parseFloat(RegExp.$1);
                    var longitude = parseFloat(RegExp.$2);

                    $rootScope.$broadcast('coordService: updated', { coordinate: [ longitude, latitude ] } );

                }
                else if ( location.match( mgrsPattern ) ) {
                    var mgrsString = RegExp.$1 + RegExp.$2 + RegExp.$3 + RegExp.$4 + RegExp.$5 + RegExp.$6;
                    var coords = mgrs.toPoint(mgrsString);

                    $rootScope.$broadcast('coordService: updated', { coordinate: coords } );

                }
                else if ( location.match( bePattern ) && AppO2.APP_CONFIG.params.misc.beLookupEnabled ) {
                    var columnName = AppO2.APP_CONFIG.params.misc.placemarks.columnName;
                    var tableName = AppO2.APP_CONFIG.params.misc.placemarks.tableName;

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
                        url: encodeURI( wfsUrl )
                    }).then(function( response ) {
                        var features = response.data.features;

                        if ( features.length > 0 ) {
                            $rootScope.$broadcast( 'coordService: updated', features[0].geometry.coordinates );
                        }
                        else {
                            $rootScope.$broadcast('coordService: be_search_error', "Sorry, we couldn't find a matching BE number for this image.");
                        }
                    });
                }
                else {
                    var baseUrl = "http://o2.radiantbluecloud.com/o2-omar"; //AppO2.APP_CONFIG.serverURL;
                    var twoFishesProxy = AppO2.APP_CONFIG.params.twofishes.proxy;
                    var twoFishesUrl = baseUrl + twoFishesProxy + "?" +
                        "autocompleteBias=BALANCED&" +
                        "maxInterpretations=1&" +
                        "query=" + location + "&" +
                        "responseIncludes=WKT_GEOMETRY_SIMPLIFIED";

                    $http({
                        method: 'GET',
                        url: encodeURI( twoFishesUrl )
                    }).then(function( response ) {
                        var features = response.data.interpretations;

                        if ( features.length > 0 ) {
                            var geometry = features[0].feature.geometry;
                            var center = geometry.center;

                            var params = { coordinate: [ center.lng, center.lat ] };
                            if ( geometry.bounds ) { params.bounds = geometry.bounds; }
                            $rootScope.$broadcast( 'coordService: updated', params );
                        }
                        else {
                            $rootScope.$broadcast( 'coordService: twofishes_error', "Sorry, we couldn't find anything for that location." );
                        }
                    });
                }

            }
        }
}() );
