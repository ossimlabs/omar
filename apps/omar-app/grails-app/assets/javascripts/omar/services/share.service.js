(function() {
    'use strict';
    angular
        .module('omarApp')
        .service('shareService', ['clipboardService', '$uibModal', '$rootScope', shareService]);

    function shareService(clipboardService, $uibModal, $rootScope) {
      var vm = this;

      vm.imageLinkModal = function(imageLink) {
        var modalInstance = $uibModal.open({
          templateUrl: AppO2.APP_CONFIG.serverURL + '/list/list.image-link.partial.html',
          controller: ['clipboardService', '$uibModalInstance', 'imageLink', ImageLinkModalController],
          controllerAs: 'vm',
          resolve: {
            imageLink: function () {
              return imageLink;
            },
          }
        })
      };
    }

    function ImageLinkModalController(clipboardService, $uibModalInstance, imageLink) {
      var vm = this;

      vm.imageLink = imageLink;

      vm.close = function () {
        $uibModalInstance.close();
      };
    }

}());
