(function() {
    'use strict';
    angular
        .module('omarApp')
        .controller('MapImageController', ['$scope', '$state', '$stateParams', '$http', '$location', 'beNumberService', MapImageController]);

    function MapImageController($scope, $state, $stateParams, $http, $location, beNumberService) {

        // #################################################################################
        // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
        // provides access to various client params in application.yml
        // #################################################################################
        console.log('AppO2.APP_CONFIG in MapImageController: ', AppO2.APP_CONFIG);

        /* jshint validthis: true */
        var vm = this;




    }

}());
