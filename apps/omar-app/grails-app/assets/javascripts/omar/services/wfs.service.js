(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('wfsService', ['$rootScope', '$http', '$timeout', wfsService]);

        function wfsService ($rootScope, $http, $timeout) {

            // #################################################################################
            // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It 
            // provides access to various client params in application.yml
            // #################################################################################
            //console.log('AppO2.APP_CONFIG in wfsService: ', AppO2.APP_CONFIG);
            
            //var wfsClient;
            //wfsClient = new OGC.WFS.Client(APP_CONFIG.services.omar.wfsUrl);
            //console.log(APP_CONFIG.services.omar.wfsUrl);

            //OpenLayers.ProxyHost = "/o2/proxy/index?url=";

            // TODO: getCapabilities and DescribeFeatureType to get the geometry column

            //var wfsRequestUrl = '/o2/wfs?';
            var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;
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

            // When this changes it needs to be passed to the executeWfsQuery method
            this.spatialObj = {
                filter: ""
            };

            // When this changes it needs to be passed to the executeWfsQuery method
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

            this.updateAttrFilter = function(filter, sortField, sortType) {

                this.attrObj.filter = filter;

                if (sortField !== undefined) {

                    this.attrObj.sortField = sortField;

                }

                if (sortType !== undefined) {

                    this.attrObj.sortType = sortType;

                }

                //if (filter !== undefined){
                //    console.log('filter !== undefined');
                //    this.attrObj.filter = filter;
                //}
                //else if (sortField !== undefined || sortType !== undefined){
                //    console.log("sortField !==, sortType !==");
                //    this.attrObj.sortField = sortField;
                //    this.attrObj.sortType = sortType;
                //}

                $rootScope.$broadcast(
                    'attrObj.updated', filter
                );

                //console.log('updateAttrFilter filter', filter);
                //console.log('updateAttrFilter sortField', sortField);
                //console.log('updateAttrFilter sortType', sortType);

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

                $http({
                    method: 'GET',
                    url: url
                })
                .then(function(response) {
                    var data;
                    data = response.data.features;

                    // $timeout needed: http://stackoverflow.com/a/18996042
                    $timeout(function(){
                        $rootScope.$broadcast('wfs: updated', data);
                        //console.log('data object...', data);
                    });
                });

            };

            this.executeWfsTrendingThumbs = function(trendData) {

                var wfsImagesList = [];
                trendData.data.itemScores.filter(function(el){

                    //console.log(el);
                    wfsImagesList.push(el.item);

                });

                var wfsImageString = wfsImagesList.join(",");

                var wfsRequest = {
                    typeName: 'omar:raster_entry',
                    namespace: 'http://omar.ossim.org',
                    version: '1.1.0',
                    outputFormat: 'JSON',
                    cql: '',
                };

                wfsRequest.cql = 'id in(' + wfsImageString + ')';

                //var wfsRequestUrl = APP_CONFIG.services.omar.wfsUrl + "?";
                var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;;

                // TODO: Refactor and use string from other wfs method
                var wfsUrl = wfsRequestUrl +
                    "service=WFS" +
                    "&version=" + wfsRequest.version +
                    "&request=GetFeature" +
                    "&typeName=" + wfsRequest.typeName +
                    "&filter=" + wfsRequest.cql +
                    "&outputFormat=" + wfsRequest.outputFormat;

                var url = encodeURI(wfsUrl);

                $http({
                    method: 'GET',
                    url: url
                })
                .then(function(response) {
                    var data;
                    data = response.data.features;
                    console.log('data from wfs', data);

                    $timeout(function(){

                        $rootScope.$broadcast('wfsTrendingThumb: updated', data);

                    });

                });

            };


        }

}());