(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('ListController', ['wfsService', '$stateParams', '$uibModal', 'mapService', 'imageSpaceService', 'jpipService', '$scope', '$http', '$location', ListController]);

    function ListController(wfsService, $stateParams, $uibModal, mapService, imageSpaceService, jpipService, $scope, $http, $location) {

            // #################################################################################
            // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
            // provides access to various client params in application.yml
            // #################################################################################
            //console.log('AppO2.APP_CONFIG in HomeController: ', AppO2.APP_CONFIG);

            /* jshint validthis: true */
            var vm = this;

            // TODO: Move to external config
            //vm.thumbPath = '/o2/imageSpace/getThumbnail?';
            vm.thumbPath = AppO2.APP_CONFIG.params.thumbnails.baseUrl;
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
                    case "mpeg":
                        border["border-color"] = "#A4FEFF"; // red
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

            // Shows/Hides the KML SuperOverlay button based on parameters passed down
            // from application.yml
            vm.kmlSuperOverlayAppEnabled = AppO2.APP_CONFIG.params.kmlApp.enabled;

            if (vm.kmlSuperOverlayAppEnabled) {
                vm.kmlSuperOverlayLink = AppO2.APP_CONFIG.params.kmlApp.baseUrl;

            }

            // Shows/Hides the jpip stream button based on parameters passed down
            // from application.yml
            vm.jpipAppEnabled = AppO2.APP_CONFIG.params.jpipApp.enabled;
            if (vm.jpipAppEnabled) {
                vm.jpipLink = AppO2.APP_CONFIG.params.jpipApp.baseUrl;
            }

            //AppO2.APP_PATH is passed down from the .gsp
            // {{list.o2baseUrl}}/#{{list.o2contextPath}}/mapOrtho?layers={{image.properties.id}}
            //http://localhost/omar-app/omar/#/mapOrtho?layers=118
            vm.o2baseUrl = AppO2.APP_CONFIG.serverURL + '/omar';
            console.log('vm.o2baseUrl: ', vm.o2baseUrl);
            //vm.o2contextPath = AppO2.APP_CONTEXTPATH;

            vm.displayFootprint = function(obj){

                mapService.mapShowImageFootprint(obj);

            };

            vm.removeFootprint = function () {

                mapService.mapRemoveImageFootprint();

            };

            vm.getJpipStream = function ($event, file, entry) {
               var TRACE = 0;
               if ( TRACE )
               {
                  console.log('list.getJpipStream entered...');
                  console.log('file: ' + file);
                  console.log('entry: ' + entry);
               }
               
               // Get the jpip stream. 3rd arg is projCode.  chip=image space.
               jpipService.getJpipStream($event, file, entry, 'chip');

               if ( TRACE )
               {
                  console.log('list.getJpipStream exited...');
               }
            };
            

            vm.currentAttrFilter = wfsService.attrObj;
            vm.currentSortText = "Acquired (Newest)";

            vm.currentStartIndex = 1; //wfsService.attrObj.startIndex;
            vm.itemsPerPage = 10;

            vm.pagingChanged = function(){

                //console.log('Page changed');

            };

            vm.sortWfs = function(field, type, text) {

                // Sets the text of the current sort method on the sort navbar
                vm.currentSortText = text;

                //wfsService.updateAttrFilter(undefined, field, type);
                wfsService.updateAttrFilter(wfsService.attrObj.filter, field, type);

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
                    templateUrl: AppO2.APP_CONFIG.serverURL + '/list/list.image-card.partial.html',
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

            vm.pioAppEnabled = AppO2.APP_CONFIG.params.predio.enabled;
            vm.logRatingToPio = function(imageId) {

                if (vm.pioAppEnabled) {
                    console.log(vm.pioAppEnabled);

                    //console.log('logRating imageId param:', imageId);

                    var pioUrl = AppO2.APP_CONFIG.params.predio.baseUrl + 'viewItem?targetEntityId=' + imageId;
                    $http({
                        method: 'POST',
                        url: pioUrl
                    })
                    .then(function(response) {

                        var data;
                        data = response;  // callback response from Predictive IO controller
                        console.log('rating response', data);

                    },
                    function error(response) {

                        console.log('failed', response); // supposed to have: data, status, headers, config, statusText

                    });

                }

            };

        }

        // Handles the selected image modal obj
        function ImageModalController($uibModalInstance, imageSpaceService, imageObj){

            var vm = this;

            vm.selectedImage = imageObj;
            //console.log(vm.selectedImage);

            //modal.rendered = false;
            //console.log('modal', modal);

            //AppO2.APP_PATH is passed down from the .gsp
            vm.o2baseUrlModal = AppO2.APP_CONFIG.serverURL + '/omar';
            //vm.o2contextPathModal = AppO2.APP_CONTEXTPATH;

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
