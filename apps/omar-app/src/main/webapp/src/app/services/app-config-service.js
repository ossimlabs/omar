'use strict';
angular
    .module('omarApp')
    .service('appConfigService', appConfigService);

    function appConfigService ($http, $q, $log) {

        console.log('appConfigService firing!');

        var deferred = $q.defer();

        this.getConfig = function() {

            $http({
                url: '../config.json',
                method: 'GET'
            }).then(function successCallback(response) {
                    console.log('response', response.data);
                    deferred.resolve(response.data);
                }, function errorCallback(response){
                    $log.error('Error requesting config parameters.', response.error);
                    deferred.reject(error);
                });
            return deferred.promise;
        };
        //this.getConfig();

    }