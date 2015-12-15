(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('ListController', ['APP_CONFIG', 'wfsService', '$stateParams', '$uibModal', 'imageSpaceService', ListController]);

        function ListController(APP_CONFIG, wfsService, $stateParams, $uibModal, imageSpaceService) {

            /* jshint validthis: true */
            var vm = this;

            var omarUrl = APP_CONFIG.services.omar.url;
            var omarPort = APP_CONFIG.services.omar.port || '80';
            var omarThumbnails = APP_CONFIG.services.omar.thumbnailsUrl;

            vm.omarThumbnailsUrl = omarUrl + ':' + omarPort + omarThumbnails;
            //console.log('vm.omarThumbnailsUrl', vm.omarThumbnailsUrl);

            var wfsRequestObj = {};

            wfsRequestObj.maxFeatures = $stateParams.maxFeatures;
            wfsRequestObj.cql = $stateParams.cql;

            wfsService.executeWfsQuery(wfsRequestObj);

            var promise = wfsService.getWfsResults();

            promise.then(function(data){
                vm.wfsData = data;
                //$log.warn('wfsData', vm.wfsData);
                //$log.warn('wfsData.length', vm.wfsData.length);

            });

            vm.showImageModal = showImageModal;

            function showImageModal(imageObj) {
                //console.log('imageObj', imageObj);
                $uibModal.open({
                    size: 'lg',
                    templateUrl: '/list/list.image.partial.html',
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
                    //alert("Hello");
                    imageSpaceService.initImageSpaceMap(imageSpaceObj);
                }, 100);
                //imageSpaceService.initImageSpaceMap(imageSpaceObj);
            });

         }

})();
