(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('wfsService', ['APP_CONFIG', '$rootScope', '$http', '$timeout', wfsService]);

        function wfsService (APP_CONFIG, $rootScope, $http, $timeout) {

            //var wfsClient;
            //wfsClient = new OGC.WFS.Client(APP_CONFIG.services.omar.wfsUrl);
            //console.log(APP_CONFIG.services.omar.wfsUrl);

            //OpenLayers.ProxyHost = "/o2/proxy/index?url=";

            // TODO: getCapabilities and DescribeFeatureType to get the geometry column
            var wfsRequestUrl = APP_CONFIG.services.omar.wfsUrl + "?";
            var wfsRequest = {
                typeName: 'omar:raster_entry',
                namespace: 'http://omar.ossim.org',
                version: '1.1.0',
                outputFormat: 'JSON',
                cql: '',
                sortField: 'acquisition_date',
                sortType: '+D',
                startIndex: '0',
                maxFeatures: '1000'
            };

            // When this changea ir needa to be passed to the executeWfsQuery method
            this.spatialObj = {
                filter: ""
            };

            // When this changea ir needa to be passed to the executeWfsQuery method
            this.attrObj = {
                filter: "",
                sortField: "acquisition_date",
                sortType: "+D",
                startIndex: 0,
            };

            this.updateSpatialFilter = function(filter) {
                this.spatialObj.filter = filter;
                $rootScope.$broadcast(
                    'spatialObj.updated', filter
                );
                //console.log('updateSpatialFilter param', filter);
            };

            // wfsService.updateAttrFilter(filterString, undefined, undefined);

            this.updateAttrFilter = function(filter, sortField, sortType, startIndex) {

                if (filter !== undefined){
                    //console.log('filter !== undefined');
                    this.attrObj.filter = filter;
                }
                else if (sortField !== undefined || sortType !== undefined){
                    //console.log("sortField !==, sortType !==");
                    this.attrObj.sortField = sortField;
                    this.attrObj.sortType = sortType;
                }
                else if (startIndex !== undefined){
                    this.attrObj.startIndex = startIndex;
                    console.log('...updating startIndex...');
                }

                $rootScope.$broadcast(
                    'attrObj.updated', filter
                );
                //console.log('updateAttrFilter filter', filter);
                //console.log('updateAttrFilter sortField', sortField);
                //console.log('updateAttrFilter sortType', sortType);
                //console.log('updateAttrFilter startIndex', startIndex);
            };

            this.executeWfsQuery = function() {

                //console.log('spatialObj inside of executeWfsQuery', spatialObj);
                //console.log('attrObj inside of executeWfsQuery', this.attrObj);

                //console.log('spatialParam', spatialParam);
                //console.log('filterParam', filterParam);

                // Only send the spatialObj to filter the results
                if (this.attrObj.filter === ""){

                    //console.log('filterParam === ""');
                    wfsRequest.cql = this.spatialObj.filter;

                }
                // Filter the results using the spatialObj and the attrObj
                else {

                    wfsRequest.cql = this.spatialObj.filter + " AND " + this.attrObj.filter;
                    //console.log('wfsRequest Object', wfsRequest);

                }

                wfsRequest.sortField = this.attrObj.sortField;
                wfsRequest.sortType = this.attrObj.sortType;
                wfsRequest.startIndex = this.attrObj.startIndex;
                //wfsRequest.maxFeatures = this.attrObj.maxFeatures;

                //console.log('wfsRequest', wfsRequest);

                //wfsClient.getFeature(wfsRequest, function(data) {
                //
                //    console.log('getFeature data', data);
                //
                //    $rootScope.$broadcast('wfs: updated', data);
                //
                //});

                //console.log('wfsRequest object...', wfsRequest);

                var wfsUrl = wfsRequestUrl +
                    "service=WFS" +
                    "&version=" + wfsRequest.version +
                    "&request=GetFeature" +
                    "&typeName=" + wfsRequest.typeName +
                    "&filter=" + wfsRequest.cql +
                    "&outputFormat=" + wfsRequest.outputFormat +
                    "&sortBy=" + wfsRequest.sortField + wfsRequest.sortType +
                    "&startIndex=" + wfsRequest.startIndex; // +
                    //"&maxFeatures=" + wfsRequest.maxFeatures;

                var url = encodeURI(wfsUrl);
                //alert(url);


                //var items = [];
                //var busy = false;
                //var after = '';

                $http({
                    method: 'GET',
                    url: url
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

                    //for (var i = 0; i < items.length; i++){
                    //    items.push(items[i].response);
                    //}

                    // $timeout needed: http://stackoverflow.com/a/18996042
                    $timeout(function(){
                        $rootScope.$broadcast('wfs: updated', data);
                        //console.log('data object...', data);
                    });
                });

            };

        }

}());