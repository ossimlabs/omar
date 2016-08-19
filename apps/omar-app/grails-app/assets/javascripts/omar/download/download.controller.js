(function(){
  'use strict';
  angular
      .module('omarApp')
      .controller('DownloadController', ['$stateParams', '$scope', 'toastr', '$http', DownloadController]);

      function DownloadController($stateParams, $scope, toastr, $http) {

        var vm = this;
        var imageLayerIds;

        vm.downloadFiles = function(imageId)
        {
          if(!imageId)
          {
            imageLayerIds = $stateParams.layers.split( "," );
            imageId = imageLayerIds[0];
          }

          var downloadManager = AppO2.APP_CONFIG.params.downloadApp.baseUrl
          var dataManager = AppO2.APP_CONFIG.params.stagerApp.baseUrl
          dataManager = dataManager+"/dataManager/getRasterFiles?id="+imageId
          downloadManager = downloadManager+"/archive/download"

          $http({
              method: 'GET',
              url: dataManager
          })
          .then(function(response)
          {
            if(response.data.results.length > 0)
            {
              var data =
                {
                  "type":"Download",
                  "zipFileName": "",
                  "archiveOptions":
                  {
                    "type": "zip"
                  },
                  "fileGroups":
                  [
                    {
                      "rootDirectory":"",
                      "files":[""]
                    }
                  ]
                }

                data.fileGroups[0].files = response.data.results

                $.fileDownload(
                  downloadManager,
                  {
                    httpMethod: "POST",
                    dataType: "text",
                    contentType: "plain/text",
                    data:{fileInfo:JSON.stringify(data)},
                    successCallback: function (url)
                    {
                      toastr.success("Files are being downloaded.",
                      {
                        positionClass: 'toast-bottom-left',
                        closeButton: true,
                        timeOut: 10000,
                        extendedTimeOut: 5000,
                        target: 'body'
                      });
                    },
                    failCallback: function(responseHtml, url, error)
                    {
                      //Error will occur if type and archiveOptions type is not specified
                      toastr.error("Unable to download with URL = " + url,
                      {
                        positionClass: 'toast-bottom-left',
                        closeButton: true,
                        timeOut: 10000,
                        extendedTimeOut: 5000,
                        target: 'body'
                      });
                    }
                  });
            }
            else
            {
              toastr.error("No files available to download.")
            }
          },
          function error(response)
          {
            toastr.error("Unable to download files.",
            {
              positionClass: 'toast-bottom-left',
              closeButton: true,
              timeOut: 10000,
              extendedTimeOut: 5000,
              target: 'body'
            });
            console.log('failed', response); // supposed to have: data, status, headers, config, statusText
          });
        }
      }
})();
