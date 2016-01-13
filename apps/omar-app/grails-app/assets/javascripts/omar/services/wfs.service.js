(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('wfsService', ['APP_CONFIG', '$rootScope', '$http', '$timeout', wfsService]);

        function wfsService (APP_CONFIG, $rootScope, $http, $timeout) {

            var wfsClient;
            wfsClient = new OGC.WFS.Client(APP_CONFIG.services.omar.wfsUrl);
            console.log(APP_CONFIG.services.omar.wfsUrl);

            OpenLayers.ProxyHost = "/o2/proxy/index?url=";

            // TODO: getCapabilities and DescribeFeatureType to get the geometry column
            var wfsRequestUrl = APP_CONFIG.services.omar.wfsUrl + "?";
            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                maxFeatures: '200',
                outputFormat: 'JSON',
                cql: '',
                sortField: 'acquisition_date',
                sortType: '+D'
            };

            // When these change they need to be passed to the executeWfsQuery method
            var spatialObj = {
                filter: ""
            };

            var attrObj = {
                filter: ""
            };

            this.updateSpatialFilter = function(filter) {
                spatialObj.filter = filter;
                $rootScope.$broadcast(
                    'spatialObj.filter.updated', filter
                );
                //console.log('updateSpatialFilter param', filter);
            };

            this.updateAttrFilter = function(filter) {
                attrObj.filter = filter;
                $rootScope.$broadcast(
                    'attrObj.filter.updated', filter
                );
                //console.log('updateAttrFilter param', filter);
            };

            this.executeWfsQuery = function() {

                //console.log('spatialObj inside of executeWfsQuery', spatialObj);
                console.log('attrObj inside of executeWfsQuery', attrObj);

                //console.log('spatialParam', spatialParam);
                //console.log('filterParam', filterParam);

                if (attrObj.filter === ""){

                    //console.log('filterParam ==== ""');
                    wfsRequest.cql = spatialObj.filter;

                }
                else {

                    //console.log('filterParam != ""');
                    wfsRequest.cql = spatialObj.filter + " AND " + attrObj.filter; //spatialParam.cql + " AND
                    // file_type='nitf'";
                    console.log('wfsRequest.cql', wfsRequest.cql);

                }
                
                //console.log('wfsRequest', wfsRequest);

                //wfsClient.getFeature(wfsRequest, function(data) {
                //
                //    console.log('getFeature data', data);
                //
                //    $rootScope.$broadcast('wfs: updated', data);
                //
                //});

                wfsRequest.cql = spatialObj.filter + " AND " + attrObj.filter;
                console.log('wfsRequest object...', wfsRequest);

                var wfsUrl = wfsRequestUrl +
                    "&service=WFS" +
                    "&version=" + wfsRequest.version +
                    "&request=GetFeature" +
                    "&typeName=" + wfsRequest.typeName +
                    "&filter=" + wfsRequest.cql +
                    "&outputFormat=" + wfsRequest.outputFormat; /*+
                    "&sortBy=acquistion_date+D"; */

                console.log('wfsUrl ...', wfsUrl);

                $http({
                    method: 'GET',
                    url: wfsUrl
                    //url: wfsRequestUrl, // +
                    // "&version=1.1.0&request=GetFeature&typeName=omar:raster_entry&outputFormat=json",
                    //params: {
                    //    filter: wfsRequest.cql,
                    //    outputFormat: wfsRequest.outputFormat,
                    //    typeName: wfsRequest.typeName,
                    //    request: 'GetFeature',
                    //    version: wfsRequest.version,
                    //    service: 'WFS',
                    //    //sortBy: 'acquisition_date+D'
                    //}
                })
                .then(function(response) {
                    var data;
                    data = response.data.features;
                    // $timeout needed: http://stackoverflow.com/a/18996042
                    $timeout(function(){
                        $rootScope.$broadcast('wfs: updated', data);
                        console.log('data object...', data);
                    });
                });

            };

        }

}());