(function() {
    'use strict';
    angular
        .module( 'omarApp' )
        .controller( 'WFSOutputDlController', ['wfsService', '$window', WFSOutputDlController]);

    function WFSOutputDlController( wfsService, $window )
    {
      var vm = this;
      vm.getDownloadURL = function( outputFormat )
      {
        var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;
        var version = '1.1.0';
        var typeName = 'omar:raster_entry';
        var wfsUrl = wfsRequestUrl +
                    'service=WFS' +
                    '&version=' + version +
                    '&request=GetFeature' +
                    '&typeName=' + typeName +
                    '&filter=' + wfsService.spatialObj.filter +
                    '&outputFormat=' + outputFormat +
                    '&sortBy=' + wfsService.attrObj.sortField + wfsService.attrObj.sortType +
                    '&startIndex=' + wfsService.attrObj.startIndex;
        vm.url = encodeURI( wfsUrl );
        $window.open( vm.url.toString(), '_blank' );
      };
    }
})();
