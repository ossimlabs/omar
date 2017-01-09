(function() {
    'use strict';
    angular
        .module( 'omarApp' )
        .service( 'shareService', ['clipboardService', '$uibModal', shareService]);

    function shareService( clipboardService, $uibModal ) {

      this.imageLinkModal = function( imageLink, title ) {
        var modalInstance = $uibModal.open( {
          templateUrl: AppO2.APP_CONFIG.serverURL + '/views/list/list.image-link.partial.html',
          controller: ['clipboardService', '$uibModalInstance', 'imageLink', 'title', ImageLinkModalController],
          controllerAs: 'vm',
          resolve: {
            imageLink: function() {
              return imageLink;
            },
            title: function() {
              return title;
            }
          }
        });
      };
    }

    function ImageLinkModalController( clipboardService, $uibModalInstance, imageLink, title ) {

      if (title === undefined) {

        this.shareModalTitle = 'Share Image';

      } else {

        this.shareModalTitle = title;

      }

      this.imageLink = imageLink;
      this.emailLink = encodeURIComponent( imageLink );

      this.close = function() {

        $uibModalInstance.close();

      };

    }
}() );
