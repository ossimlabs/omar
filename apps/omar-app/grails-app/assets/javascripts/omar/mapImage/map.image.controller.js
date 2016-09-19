(function () {
  'use strict';
  angular
  .module('omarApp')
  .controller('MapImageController', ['$scope', '$state', '$stateParams', '$http', '$location', 'imageSpaceService', 'beNumberService', MapImageController]);

  function MapImageController($scope, $state, $stateParams, $http, $location, imageSpaceService, beNumberService) {

    /* jshint validthis: true
    var vm = this; */

    var imageSpaceObj = {};

    if ($stateParams) {
      imageSpaceObj = {
          filename: $stateParams.filename,
          entry: $stateParams.entry_id,
          imgWidth: $stateParams.width,
          imgHeight: $stateParams.height
        };
    }

    imageSpaceService.initImageSpaceMap(imageSpaceObj);

  }

}());
