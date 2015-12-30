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
                maxFeatures: '200',
                outputFormat: 'JSON',
                cql: ''
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

            //this.executeWfsQuery = function(spatialParam, filterParam) {
            this.executeWfsQuery = function() {

                console.log('spatialObj inside of executeWfsQuery', spatialObj);
                console.log('attrObj inside of executeWfsQuery', attrObj);

                // We need to be able to pass in the spatialFilterObj and
                // attrFilter Obj each time they are updated

                //console.log('spatialParam', spatialParam);
                //console.log('filterParam', filterParam);

                //attrObj.filter = "file_type='nitf'";

                if (attrObj.filter === ""){

                    console.log('filterParam ==== ""');
                    wfsRequest.cql = spatialObj.filter;

                }
                else {

                    console.log('filterParam != ""');
                    wfsRequest.cql = spatialObj.filter + " AND " + attrObj.filter; //spatialParam.cql + " AND
                    // file_type='nitf'";
                    console.log('wfsRequest.cql', wfsRequest.cql);

                }

                //wfsRequest.cql = spatialParam; //+ " AND file_type='tiff'");
                //console.log('wfsRequest', wfsRequest);

                wfsClient.getFeature(wfsRequest, function(data) {

                    //console.log('getFeature data', data);

                    $rootScope.$broadcast('wfs: updated', data);

                });

            };

        }

}());