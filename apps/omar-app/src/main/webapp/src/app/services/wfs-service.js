/**
 * Created by adrake on 10/19/15.
 */
'use strict';
angular
    .module('omarApp')
    .service('wfsService', wfsService)

    function wfsService ($q) {

        var wfsClient;
        wfsClient = new OGC.WFS.Client('http://localhost:8080/wfs');

        OpenLayers.ProxyHost = "/proxy/index?url="

        var deferred = $q.defer();

        var wfsRequest = {
            typeName: 'omar:raster_entry',
            namespace: 'http://omar.ossim.org',
            version: '1.1.0',
            maxFeatures: 50,
            outputFormat: 'JSON'
        };

        wfsClient.getFeature(wfsRequest, function(data) {
            deferred.resolve(data);
        });

        this.getWfs = function () {
            return deferred.promise;
        }


    }