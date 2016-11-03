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

      vm.goToTLV = function()
      {
        var tlvRequestUrl = AppO2.APP_CONFIG.params.tlvApp.baseUrl;
        var tlvURL,
            pointLatLon,
            startDate,
            endDate,
            startDay = '',
            startMonth = '',
            startYear = '',
            endDay = '',
            endMonth = '',
            endYear = '';
        var listSize;
        var acquisitionDateList = [];
        var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;
        var version = '1.1.0';
        var typeName = 'omar:raster_entry';
        var wfsUrl = wfsRequestUrl +
                    'service=WFS' +
                    '&version=' + version +
                    '&request=GetFeature' +
                    '&typeName=' + typeName +
                    '&filter=' + wfsService.spatialObj.filter +
                    '&outputFormat=JSON' +
                    '&sortBy=' + wfsService.attrObj.sortField + wfsService.attrObj.sortType +
                    '&startIndex=' + wfsService.attrObj.startIndex;
        vm.url = encodeURI( wfsUrl );

        mapService.mapPointLatLon();

        if ( mapService.pointLatLon ) {
          pointLatLon = mapService.pointLatLon;
        }else {
          pointLatLon = '';
        }

        $http({
          method: 'GET',
          url: vm.url
        }).then(function successCallback( response ) {
          var data = response.data.features;
          angular.forEach( data, function( image ) {
            if ( image.properties.acquisition_date ) {
              acquisitionDateList.push( image.properties.acquisition_date );
            }
          });

          listSize = acquisitionDateList.length;

          if ( listSize > 0 ) {
            acquisitionDateList.sort();

            startDate = new Date( acquisitionDateList[0] );
            endDate = new Date( acquisitionDateList[listSize - 1] );

            startDay = startDate.getDate();
            startMonth = startDate.getMonth() + 1;
            startYear = startDate.getFullYear();
            endDay = endDate.getDate();
            endMonth = endDate.getMonth() + 1;
            endYear = endDate.getFullYear();

            tlvURL =  encodeURI( tlvRequestUrl +
                        '/?location=' +
                        pointLatLon +
                        '&startDay=' + startDay +
                        '&startMonth=' + startMonth +
                        '&startYear=' + startYear +
                        '&endDay=' + endDay +
                        '&endMonth=' + endMonth +
                        '&endYear=' + endYear );
        }else {
            tlvURL =  encodeURI( tlvRequestUrl +
                        '/?location=' +
                        pointLatLon  +
                        '&startYear=2000' );
        }

        $window.open( tlvURL, '_blank' );

      }, function errorCallback( response ) {
          console.log( 'ERROR' );
        });
      }; //end goToTLV
    }
})();
