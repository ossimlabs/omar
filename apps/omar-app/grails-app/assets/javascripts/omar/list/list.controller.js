(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('ListController', ['APP_CONFIG', 'wfsService', '$stateParams', '$uibModal', 'imageSpaceService', '$scope', ListController]);

        function ListController(APP_CONFIG, wfsService, $stateParams, $uibModal, imageSpaceService, $scope) {

            /* jshint validthis: true */
            var vm = this;

            vm.thumbPath = '/o2/imageSpace/getThumbnail?';
            vm.thumbFilename = 'filename='; // parameter provided by image.properties.filename
            vm.thumbEntry = '&entry=';  // parameter provided by image.properties.entry_id
            vm.thumbSize = '&size=64';
            vm.thumbFormat = '&format=jpeg';

            $scope.$on('wfs: updated', function(event, data) {

                $scope.$apply(function(){
                    vm.wfsData = data;
                });

            });

            vm.showImageModal = showImageModal;

            function showImageModal(imageObj) {

                //console.log('imageObj', imageObj);

                $uibModal.open({
                    size: 'lg',
                    templateUrl: '/o2/list/list.image-card.partial.html',
                    controller: ['$uibModalInstance', 'imageSpaceService', 'imageObj', ImageModalController],
                    controllerAs: 'vm',
                    resolve: {
                        imageObj: function() { return imageObj; },
                    }
                });

            }
        }

        // Handles the selected image modal obj
        function ImageModalController($uibModalInstance, imageSpaceService, imageObj){

            var vm = this;

            vm.selectedImage = imageObj;
            //console.log(vm.selectedImage);

            var modal = this;
            modal.rendered = false;

            var imageSpaceObj = {
                filename: imageObj.properties.filename,
                entry: imageObj.properties.entry_id,
                imgWidth: imageObj.properties.width,
                imgHeight: imageObj.properties.height
            };

            $uibModalInstance.opened.then(function(){
                setTimeout(function(){
                    imageSpaceService.initImageSpaceMap(imageSpaceObj);
                }, 100);
            });

         }

})();
