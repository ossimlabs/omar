(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('ListController', ['wfsService', '$stateParams', '$uibModal', 'imageSpaceService', '$scope', ListController]);

        function ListController(wfsService, $stateParams, $uibModal, imageSpaceService,  $scope) {

            /* jshint validthis: true */
            var vm = this;

            vm.thumbPath = '/o2/imageSpace/getThumbnail?';
            vm.thumbFilename = 'filename='; // parameter provided by image.properties.filename
            vm.thumbEntry = '&entry=';  // parameter provided by image.properties.entry_id
            vm.thumbSize = '&size=100';
            vm.thumbFormat = '&format=jpeg';

            vm.currentAttrFilter = wfsService.attrObj;
            vm.currentSortText = "Acquired (Newest)";

            vm.sortWfs = function(field, type, text) {
                console.log('sortWfs firing --> field: ' + field + ' type: ' + type + ' text: ' + text);

                vm.currentSortText = text;
                wfsService.updateAttrFilter(undefined, field, type);

            };

            // We need an $on event here to listen for changes to the
            // wfs.spatial and wfs.attr filters
            $scope.$on('spatialObj.updated', function(event, filter) {

                //console.log('$on spatialObj filter updated', filter);

                //wfsService.executeWfsQuery(filter, null);
                wfsService.executeWfsQuery();

            });

            $scope.$on('attrObj.updated', function(event, filter) {

                //console.log('$on attrObj filter updated', filter);

                wfsService.executeWfsQuery();


            });

            $scope.$on('wfs: updated', function(event, data) {

                // Update the DOM (card list)
                $scope.$apply(function(){
                    vm.wfsData = data;
                });

            });

            vm.showImageModal = function(imageObj) {

                //console.log('imageObj', imageObj);

                var modalInstance = $uibModal.open({
                    size: 'lg',
                    templateUrl: '/o2/list/list.image-card.partial.html',
                    controller: ['$uibModalInstance', 'imageSpaceService', 'imageObj', ImageModalController],
                    controllerAs: 'vm',
                    resolve: {
                        imageObj: function() { return imageObj; },
                    }
                });

                modalInstance.result.then(function() {
                    console.log('Modal closed at: ' + new Date());

                }, function () {
                    console.log('Modal dismissed at: ' + new Date());
                });

            };

        }

        // Handles the selected image modal obj
        function ImageModalController($uibModalInstance, imageSpaceService, imageObj){

            var vm = this;

            vm.selectedImage = imageObj;
            //console.log(vm.selectedImage);

            //var modal = this;
            //modal.rendered = false;
            //console.log('modal', modal);

            var imageSpaceObj = {
                filename: imageObj.properties.filename,
                entry: imageObj.properties.entry_id,
                imgWidth: imageObj.properties.width,
                imgHeight: imageObj.properties.height
            };

            vm.imageMapHelpPopover = {
                zoomHotkey: 'SHIFT',
                rotateHotkey: 'SHIFT + ALT',
                templateUrl: 'imageMapHelpTemplate.html',
                title: 'Help'
            };

            vm.cancel = function(){
                console.log('closing...');
                $uibModalInstance.close('paramObj');
            };

            vm.dismiss = function(){
                console.log('dismissing...');
                $uibModalInstance.dismiss('cancel');
            };

            $uibModalInstance.opened.then(function(){
                setTimeout(function(){
                    imageSpaceService.initImageSpaceMap(imageSpaceObj);
                }, 100);
            });

         }

})();
