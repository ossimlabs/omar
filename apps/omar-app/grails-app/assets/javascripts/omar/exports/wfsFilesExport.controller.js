(function(){
    'use strict';
    angular
        .module( 'omarApp' )
        .controller('WFSOutputDlController', ['wfsService', '$scope', '$window', WFSOutputDlController]);

    function WFSOutputDlController(wfsService, $scope, $window)
    {
      var vm = this;
      //vm.currentAttr = wfsService.attrObj;
      vm.getDownloadURL = function(outputFormat)
      {
        var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;
        //var outputFormat = 'JSON';
        var version = '1.1.0';
        var typeName = 'omar:raster_entry';
        var wfsUrl = wfsRequestUrl +
                    "service=WFS" +
                    "&version=" + version +
                    "&request=GetFeature" +
                    "&typeName=" + typeName +
                    "&filter=" + wfsService.spatialObj.filter +
                    "&outputFormat=" + outputFormat +
                    "&sortBy=" + wfsService.attrObj.sortField + wfsService.attrObj.sortType +
                    "&startIndex=" + wfsService.attrObj.startIndex; // +
                    //"&maxFeatures=" + wfsRequest.maxFeatures;
        vm.url = encodeURI(wfsUrl);
        $window.open(vm.url.toString(), '_blank');
      }
    }
})();
