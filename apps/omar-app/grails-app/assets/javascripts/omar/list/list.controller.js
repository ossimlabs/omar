(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('ListController', ['APP_CONFIG', 'wfsService', '$stateParams', '$uibModal', ListController]);

        function ListController(APP_CONFIG, wfsService, $stateParams, $uibModal) {

            /* jshint validthis: true */
            var vm = this;

            var omarUrl = APP_CONFIG.services.omar.url;
            var omarPort = APP_CONFIG.services.omar.port;
            var omarThumbnails = APP_CONFIG.services.omar.thumbnailsUrl;

            vm.omarThumbnailsUrl = omarUrl + omarPort + omarThumbnails;
            //console.log('vm.omarThumbnailsUrl', vm.omarThumbnailsUrl);

            //vm.test = $stateParams.test;
            //$log.warn('stateParams', vm.test);

            var wfsRequestObj = {};

            wfsRequestObj.maxFeatures = $stateParams.maxFeatures;
            wfsRequestObj.cql = $stateParams.cql;

            wfsService.executeWfsQuery(wfsRequestObj);

            var promise = wfsService.getWfsResults();

            promise.then(function(data){
                vm.wfsData = data;
                //$log.warn('wfsData', vm.wfsData);
                //$log.warn('wfsData.length', vm.wfsData.length);

                // TODO: Wire up for individual cards.  Right now the rating is
                //       is shared amongst all cards.
                vm.rate = 0;
                vm.max = 5;
                vm.isReadonly = false;

                vm.hoveringOver = function(value) {
                    //console.log('hoverOver!');
                    vm.overStar = value;
                    vm.percent = 100 * (value / vm.max);
                };

                vm.ratingStates = [
                    {stateOn: 'glyphicon-ok-sign', stateOff: 'glyphicon-ok-circle'},
                    {stateOn: 'glyphicon-star', stateOff: 'glyphicon-star-empty'},
                    {stateOn: 'glyphicon-heart', stateOff: 'glyphicon-ban-circle'},
                    {stateOn: 'glyphicon-heart'},
                    {stateOff: 'glyphicon-off'}
                ];
            });

            //vm.setImageCardData = function(selected) {
            //    vm.cardData = selected;
            //    console.log('Setting vm.cardData', vm.cardData);
            //};

            //vm.asideState = {
            //    open: false
            //};

            //vm.openAside = function (position, backdrop) {
            //    vm.asideState = {
            //        open: true,
            //        position: position
            //    };

                //function postClose() {
                //    vm.asideState.open = false;
                //}

//                $aside.open({
//                    templateUrl: 'list.image.partial.html',
//                    placement: 'right',
//                    size: 'lg',
//                    controller: function ($scope, $uibModalInstance) {
//                        //vm.getImageCardData();
//;                        $scope.ok = function (e) {
//                            $uibModalInstance.close();
//                            e.stopPropagation();
//                        };
//                        $scope.cancel = function (e) {
//                            $uibModalInstance.dismiss();
//                            e.stopPropagation();
//                        };
//                    }
//                }).result.then(postClose, postClose);
//            };

            vm.showImageModal = showImageModal;

            function showImageModal(imageObj) {
                console.log('imageObj', imageObj);
                $uibModal.open({
                    size: 'lg',
                    templateUrl: 'src/app/list/list.image.partial.html',
                    controller: ['$uibModalInstance', 'imageObj', ImageModalController],
                    controllerAs: 'vm',
                    resolve: {
                        imageObj: function() { return imageObj; }
                    }
                });
            }
        }

        // Handles the selected image modal obj
        function ImageModalController($uibModalInstance, imageObj){

            //console.log('ImageModalController imageObj', imageObj);
            var vm = this;

            vm.selectedImage = imageObj;
            console.log(vm.selectedImage);

         }

})();
