(function () {
    'use strict';
    angular
      .module('omarApp')
      .service('beNumberService',['$rootScope', '$http', beNumberService]);

    function beNumberService($rootScope, $http) {

      var beObj = {}

      this.getBeData =  function(geom) {
        console.log('Calling getBeData with: ', geom);

        beObj.prop1 = "Some really cool beData";

        return beObj;

      }

    }

}());
