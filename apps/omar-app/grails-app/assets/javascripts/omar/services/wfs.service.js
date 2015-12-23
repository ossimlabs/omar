(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('wfsService', ['APP_CONFIG', '$rootScope', wfsService]);

        function wfsService (APP_CONFIG, $rootScope) {

            var wfsClient;
            wfsClient = new OGC.WFS.Client(APP_CONFIG.services.omar.wfsUrl);

            OpenLayers.ProxyHost = "/o2/proxy/index?url=";

            // TODO: getCapabilities and DescribeFeatureType to get the geometry column

            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                maxFeatures: '50',
                outputFormat: 'JSON',
                cql: ''
            };

            //console.log('wfsRequest', wfsRequest);
            this.setWfsParams = function(params) {
                console.log(params);
                wfsRequest.cql = params;
            };

            this.executeWfsQuery = function(spatialParam, filterParam) {

                console.log('spatialParam', spatialParam);
                console.log('filterParam', filterParam);

                //if (filterParam === null){
                //
                //    console.log('filterParam null');
                //    wfsRequest.cql = spatialParam.cql;
                //
                //}
                //else {
                //
                //    console.log('filterParam is NOT null');
                //    wfsRequest.cql = spatialParam.cql + " AND file_type='nitf'";
                //
                //}

                //console.log(wfsRequest.cql = spatialParam.cql + " AND file_type='tiff'");
                console.log('wfsRequest', wfsRequest);

                wfsClient.getFeature(wfsRequest, function (data) {

                    console.log('getFeature data', data);

                    $rootScope.$broadcast('wfs: updated', data);

                });

            };

        }

}());