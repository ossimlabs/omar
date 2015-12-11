(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('imageSpaceService', ['APP_CONFIG', imageSpaceService]);

        function imageSpaceService(APP_CONFIG){

            this.testImageSpace = function(){
                console.log('APP_CONFIG in imageSpaceService', APP_CONFIG);
            };

        }

}());
