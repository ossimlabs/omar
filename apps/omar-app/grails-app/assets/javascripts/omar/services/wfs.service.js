(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('wfsService', ['APP_CONFIG', '$q', '$rootScope', wfsService]);

        function wfsService (APP_CONFIG, $q, $rootScope) {

            //var self = this;

            var wfsClient;
            wfsClient = new OGC.WFS.Client(APP_CONFIG.services.omar.wfsUrl);

            OpenLayers.ProxyHost = "/proxy/index?url=";

            var deferred = $q.defer();

            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                maxFeatures: '200',
                outputFormat: 'JSON',
                cql: ''
            };

            //console.log('wfsRequest', wfsRequest);

            // private variable
            //var _wfsDataObj = {};
            //this.wfsDataObj = _wfsDataObj;

            this.executeWfsQuery = function(paramObj) {

                //console.log('paramObj', paramObj);

                // This is from mapService, the first time through this is not used
                wfsRequest.cql = paramObj.cql;

                wfsClient.getFeature(wfsRequest, function (data) {

                    console.log('getFeature data', data);

                    deferred.resolve(data);
                    $rootScope.$broadcast('wfs: updated', data);

                });

            };

            this.getWfsResults = function () {

                console.log('gettingWfsResults');
                return deferred.promise;

            };

        }

}());