(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('wfsService', ['APP_CONFIG', '$rootScope', wfsService]);

        function wfsService (APP_CONFIG, $rootScope) {

            var wfsClient;
            wfsClient = new OGC.WFS.Client(APP_CONFIG.services.omar.wfsUrl);

            OpenLayers.ProxyHost = "/o2/proxy/index?url=";

            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                maxFeatures: '50',
                outputFormat: 'JSON',
                cql: ''
            };

            //console.log('wfsRequest', wfsRequest);

            this.executeWfsQuery = function(spatialParam, filterParam) {

                console.log('spatialParam', spatialParam);
                wfsRequest.cql = spatialParam.cql;

                wfsClient.getFeature(wfsRequest, function (data) {

                    console.log('getFeature data', data);

                    $rootScope.$broadcast('wfs: updated', data);

                });

            };

        }

}());