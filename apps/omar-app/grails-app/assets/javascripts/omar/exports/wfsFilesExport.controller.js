(function() {
    'use strict';
    angular
        .module( 'omarApp' )
        .controller( 'WFSOutputDlController', ['wfsService', '$http', 'mapService', '$scope', 'toastr', '$window', WFSOutputDlController]);

    function WFSOutputDlController( wfsService, $http, mapService, $scope, toastr, $window )
    {
      var vm = this;
      vm.attrFilter = "";

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
            '&filter=' + encodeURIComponent( wfsService.spatialObj.filter ) +
            '&outputFormat=' + outputFormat +
            '&sortBy=' + wfsService.attrObj.sortField + wfsService.attrObj.sortType +
            '&startIndex=' + wfsService.attrObj.startIndex;
        vm.url = wfsUrl;
        $window.open( vm.url.toString(), '_blank' );
      };

      $scope.$on( 'attrObj.updated', function( event, response ) { vm.attrFilter = response; } );
      vm.goToTLV = function() {
        var tlvBaseUrl = AppO2.APP_CONFIG.params.tlvApp.baseUrl;
        var filter = wfsService.spatialObj.filter;
        if (filter == '') { toastr.error( "A spatial filter needs to be enabled." ); }
        else {
            var pointLatLon;
            mapService.mapPointLatLon();
            if ( mapService.pointLatLon ) {
              pointLatLon = mapService.pointLatLon;
            } else {
              var center = mapService.getCenter();
              pointLatLon = center.slice().reverse().join( ',' );
            }

            var bbox = mapService.calculateExtent().join( ',' );
            if ( vm.attrFilter ) { filter += " AND " + vm.attrFilter; }
            var tlvURL = tlvBaseUrl + '/?bbox=' + bbox + '&filter=' + encodeURIComponent( filter ) + '&location=' + pointLatLon + '&maxResults=100';
            $window.open( tlvURL, '_blank' );
        }
      };
    }
})();
