(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('ListController', ['wfsService', '$stateParams', '$uibModal', 'imageSpaceService', '$scope', '$http', ListController]);

        function ListController(wfsService, $stateParams, $uibModal, imageSpaceService, $scope, $http) {

            /* jshint validthis: true */
            var vm = this;

            vm.thumbPath = '/o2/imageSpace/getThumbnail?';
            vm.thumbFilename = 'filename='; // parameter provided by image.properties.filename
            vm.thumbEntry = '&entry=';  // parameter provided by image.properties.entry_id
            vm.thumbSize = '&size=100';
            vm.thumbFormat = '&format=jpeg';

            vm.thumbBorder = function(imageType) {
                //console.log(imageType);

                var border = {
                    "border-color": "white",
                    "border-width": "1px",
                    "border-style": "solid",
                    "border-radius": "4px"
                };

                switch(imageType){
                    case "adrg":
                        border["border-color"] = "#326F6F"; // atoll
                        break;
                    case "aaigrid":
                        border["border-color"] = "pink";
                        break;
                    case "cadrg":
                        border["border-color"] = "#00FFFF"; // cyan
                        border["border-width"] = "2px"; // makes it look the same size as others
                        break;
                    case "ccf":
                        border["border-color"] = "#8064FF"; // light slate blue
                        break;
                    case "cib":
                        border["border-color"] = "#008080"; // teal
                        border["border-width"] = "2px"; // makes it look the same size as others
                        break;
                    case "doqq":
                        border["border-color"] = "purple";
                        break;
                    case "dted":
                        border["border-color"] = "#00FF00"; // green
                        break;
                    case "imagine_hfa":
                        border["border-color"] = "lightGrey";
                        //border["border-width"] = "1.5px"; // makes it look the same size as others
                        break;
                    case "jpeg":
                        border["border-color"] = "#FFFF00"; // yellow
                        break;
                    case "jpeg2000":
                        border["border-color"] = "#FFC800"; // orange
                        break;
                    case "landsat7":
                        border["border-color"] = "#FF00FF"; // pink
                        break;
                    case "mrsid":
                        border["border-color"] = "#00BC00"; // light green
                        break;
                    case "nitf":
                        border["border-color"] = "#0000FF"; // blue
                        break;
                    case "tiff":
                        border["border-color"] = "#FF0000"; // red
                        break;
                    case "unspecified":
                        border["border-color"] = "white";
                        //border["border-width"] = "1.5px"; // makes it look the same size as others
                        break;
                    default:
                        border["border-color"] = "white";
                        //border["border-width"] = "2px"; // makes it look the same size as others

                }

                return border;
            };

            //vm.infiniteTest = function() {
            //    console.log('infinite firing!');
            //    wfsService.executeWfsQuery();
            //};

            vm.currentAttrFilter = wfsService.attrObj;
            vm.currentSortText = "Acquired (Newest)";

            vm.currentStartIndex = 1; //wfsService.attrObj.startIndex;
            vm.itemsPerPage = 10;

            vm.pagingChanged = function(){

                console.log('Page changed');

            };

            vm.sortWfs = function(field, type, text) {
                //console.log('sortWfs firing --> field: ' + field + ' type: ' + type + ' text: ' + text);

                // Sets the text of the current sort method on the sort navbar
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
                    //console.log('Modal closed at: ' + new Date());

                }, function () {
                    //console.log('Modal dismissed at: ' + new Date());
                });

            };

            vm.logRatingToPio = function(imageId){
                console.log('logRating imageId param:', imageId);

                var pioUrl = '../predio/rate?appName=omar_trending&entityId=all&targetEntityId=' + imageId + '&rating=4';
                $http({
                    method: 'GET',
                    url: pioUrl
                })
                    .then(function(response) {
                        var data;
                        data = response;  // callback response from Predictive IO controller
                        console.log('rating response', data);
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
