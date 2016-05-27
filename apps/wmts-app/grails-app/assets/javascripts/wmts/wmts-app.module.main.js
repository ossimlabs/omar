(function() {
  'use strict';

  angular
    .module('wmtsApp', ['ui.bootstrap', 'toastr'])
    .config(function(toastrConfig) {
      angular.extend(toastrConfig, {
        positionClass: 'toast-bottom-right',
      });
    });

})();
