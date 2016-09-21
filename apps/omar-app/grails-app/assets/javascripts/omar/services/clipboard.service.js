(function () {
    'use strict';
    angular
        .module('omarApp')
        .service('clipboardService', ['clipboard', '$rootScope', clipboardService]);

    function clipboardService(clipboard, $rootScope) {
      if (!clipboard.supported) {
        console.log('Sorry, copy to clipboard is not supported');
      }

      $rootScope.copyToClipboard = function (item) {
        clipboard.copyText(item);
      };
    }

  }());
