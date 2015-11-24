/**
 * Created by adrake on 10/19/15.
 */
'use strict';
angular
    .module('omarApp')
    .service('wfsService', wfsService);

    function wfsService (APP_CONFIG, $q) {
        //console.log('wfsClientUrl', APP_CONFIG.services.omar.wfsUrl);
        var wfsClient;
        //wfsClient = new OGC.WFS.Client('/wfs');
        //console.log('wfsClientUrl', APP_CONFIG.services.omar.wfsUrl);
        wfsClient = new OGC.WFS.Client(APP_CONFIG.services.omar.wfsUrl); // /wfs

        //console.log(wfsClient.convertCqlToXml(
        //    "BBOX(ground_geom, -180.0,-90.0,180.0,90.0)"
        //    "INTERSECTS(ground_geom, POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90)))"
        //));

        OpenLayers.ProxyHost = "/proxy/index?url=";

        var deferred = $q.defer();

        var wfsRequest = {
            typeName: 'omar:raster_entry',
            namespace: 'http://omar.ossim.org',
            version: '1.1.0',
            maxFeatures: 200,
            outputFormat: 'JSON',
            //cql: "INTERSECTS(ground_geom, POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90)))"
            //cql: "INTERSECTS(ground_geom, POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90)))"
            //cql: "INTERSECTS(ground_geom, POLYGON ((-180 0, -180 90, 0 90, 0 0, -180 0)))"
        };

        //if($scope.filter && $scope.filter.trim() !== ''){
        //    wfsRequest.cql = $scope.filter;
        //}

        //wfsClient.getFeature(wfsRequest, function(data) {
        //    deferred.resolve(data);
        //});

        this.executeWfsQuery = function(paramObj) {
            //console.log('paramObj', paramObj);
            wfsRequest.maxFeatures = paramObj.maxFeatures;
            wfsRequest.cql = paramObj.cql; //"INTERSECTS(ground_geom, POLYGON ((-180 0, -180 90, 0 90, 0 0, -180 0)))"

            wfsClient.getFeature(wfsRequest, function (data) {
                deferred.resolve(data);
            });
        };

        this.getWfsResults = function () {
            return deferred.promise;
        };

    }