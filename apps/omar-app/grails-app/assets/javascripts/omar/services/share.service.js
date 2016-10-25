(function() {
    'use strict';
    angular
        .module( 'omarApp' )
        .service( 'shareService', ['clipboardService', '$uibModal', shareService]);

    function shareService( clipboardService, $uibModal ) {

      this.imageLinkModal = function( imageLink ) {
        var modalInstance = $uibModal.open( {
          templateUrl: AppO2.APP_CONFIG.serverURL + '/views/list/list.image-link.partial.html',
          controller: ['clipboardService', '$uibModalInstance', 'imageLink', ImageLinkModalController],
          controllerAs: 'vm',
          resolve: {
            imageLink: function() {
              return imageLink;
            }
          }
        });
      };
    }

    function ImageLinkModalController( clipboardService, $uibModalInstance, imageLink ) {

      this.imageLink = imageLink;

      this.close = function() {
        $uibModalInstance.close();
      };
    }
}() );
