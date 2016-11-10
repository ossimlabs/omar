(function() {
    'use strict';
    angular
        .module( 'omarApp' )
        .controller( 'WFSOutputDlController', ['wfsService', '$window', '$http', 'mapService', WFSOutputDlController]);

    function WFSOutputDlController( wfsService, $window, $http, mapService )
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

      vm.goToTLV = function() {
        var tlvBaseUrl = AppO2.APP_CONFIG.params.tlvApp.baseUrl;
        var filter = wfsService.spatialObj.filter;

        var pointLatLon;
        mapService.mapPointLatLon();
        if ( mapService.pointLatLon ) {
          pointLatLon = mapService.pointLatLon;
        } else {
          var center = mapService.getCenter();
          pointLatLon = center.slice().reverse().join(', ');
        }
        var tlvURL = encodeURI( tlvBaseUrl + '/?location=' + pointLatLon + '&filter=' + filter);
        $window.open( tlvURL, '_blank' );
      };
    }
})();
