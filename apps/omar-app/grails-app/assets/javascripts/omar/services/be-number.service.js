(function ()
{
    'use strict';
    angular
        .module( 'omarApp' )
        .service( 'beNumberService', ['$rootScope', '$http', '$timeout', 'wfsService', beNumberService] );

    function beNumberService( $rootScope, $http, $timeout )
    {
        // console.log(AppO2.APP_CONFIG);
        var beObj = [];
        var placemarkConfig = AppO2.APP_CONFIG.params.misc.placemarks;
        var beLookupEnabled = (placemarkConfig) ? true : false;
        var typeName = (beLookupEnabled) ? placemarkConfig.tableName : null;
        var sortBy = (beLookupEnabled) ? placemarkConfig.columnName : null;
        var geomName = (beLookupEnabled) ? placemarkConfig.geomName : null;
        var maxFeatures = (beLookupEnabled) ? placemarkConfig.maxResults : null;

        this.getBeData = function ( geom )
        {
            // console.log('getBeData', geom);

            if ( beLookupEnabled )
            {
                // console.log( 'Calling getBeData with: ', geom );
                //
                // beObj.prop1 = "Some really cool beData";

                var bbox = geom.getExtent().join( ',' );
                var cql = "bbox(" + geomName + ", " + bbox + ")";

                // console.log( cql );

                var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;
                var wfsRequest = {
                    service: 'WFS',
                    version: '1.1.0',
                    request: 'GetFeature',
                    typeName: typeName,
                    outputFormat: 'JSON',
                    filter: cql,
                    sortBy: sortBy,
                    startIndex: '0',
                    maxFeatures: maxFeatures
                };

                var url = wfsRequestUrl + $.param( wfsRequest );

                $http( {
                    method: 'GET',
                    url: url
                } ).then( function ( response )
                {
                    var data;
                    data = response.data.features;

                    // $timeout needed: http://stackoverflow.com/a/18996042
                    $timeout( function ()
                    {
                        $rootScope.$broadcast( 'placemarks: updated', data );
                        // console.log( 'data object...', data );
                        beObj = data;
                    } );
                } );
            }

            return beObj;
        }
    }
}());
