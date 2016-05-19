(function () {
    'use strict';
    angular
        .module('omarApp')
        .service('jpipService', [ '$rootScope', '$http', 'toastr', '$timeout', jpipService ]);
    // .service('jpipService', [jpipService]);

    function jpipService( $rootScope, $http,  toastr, $timeout) {
        var TRACE = 0;
        this.serviceRunning = true;

        this.getJpipStream = function( $event, file, entry, projCode ) {

            var jpipAppEnabled = AppO2.APP_CONFIG.params.jpipApp.enabled;
            var jpipLink = "";
            var jpipServiceUrl = "";
            var MAX=240;
            var secondsEllapsed = 0;
            var timerId = 0;
            var showProcessInfo = true;
            // var $processInfo = $('.processInfo');

            if ( TRACE ) {
                console.log('jpipService.getJpipStream entered...');
                console.log( 'jpipAppEnabled: ' + jpipAppEnabled );
                console.log('file:  ' + file);
                console.log('entry: ' + entry);
                console.log('projCode: ' + projCode );
            }

            if ( jpipAppEnabled ) {
                jpipLink = AppO2.APP_CONFIG.params.jpipApp.baseUrl;

                // projCode can be: chip, geo-scaled, 4326, 3857
                jpipServiceUrl = jpipLink + '/createStream?filename=' + file + '&entry=' + entry + '&projCode=' + projCode;

                if ( TRACE ) {
                   console.log( 'jpipServiceUrl: ' +  jpipServiceUrl );
                }

                //---
                // Change the caller background color.
                //
                // var bgColorSave = $event.currentTarget.style.backgroundColor;
                // $event.currentTarget.style.backgroundColor = 'red';
                $event.currentTarget.style.opacity = 0.4;

                // Poll service until we get a finished status.

                var timerId = setInterval( function() {
                    var data;

                    // $processInfo.ng-show=true;

                    $http({ method: 'GET', url: jpipServiceUrl }).then(function(response) {
                        if ( TRACE ) {
                            data = JSON.stringify(response.data);
                            console.log('response data', data);
                        }

                        if ( response.data.status == "FINISHED" ) {
                            if ( TRACE ) {
                                console.log('FINISHED...');
                            }
                            clearInterval(timerId);
                            // $event.currentTarget.style.backgroundColor = 'green';
                            // $event.currentTarget.style.backgroundColor = bgColorSave;
                            $event.currentTarget.style.opacity = 1.0;

                            toastr.success("Jpip URL: " + response.data.url,
                                           "File: " + file,
                                            {
                                            positionClass: 'toast-bottom-left',
                                            closeButton: true,
                                            timeOut: 10000,
                                            extendedTimeOut: 5000,
                                            target: 'body'
                                        });
                            $timeout(function(){
                                $rootScope.$broadcast('jpip: updated', data);
                                //console.log('data object...', data);
                            });

                        }
                        else if ( secondsEllapsed > MAX ) {
                            toastr.error("Bummer: jpip steam conversion hit time!",
                                           "File: " + file, {
                                            positionClass: 'toast-bottom-left',
                                            closeButton: true,
                                            timeOut: 10000,
                                            extendedTimeOut: 5000,
                                            target: 'body'
                                        });

                            console.log('TODO put timeout code here...');
                        }

                       }, function error(response) {
                           console.log( JSON.stringify(response) );
                           console.log('failed', response);



                       });
                    secondsEllapsed+=2;

                    if ( TRACE ) {
                       console.log('secondsEllapsed: ' + secondsEllapsed );
                    }

                   }, 2000);

                if ( TRACE ) {
                   console.log('jpipServie.getJpipStream exited...');
                }
            }
            console.log('in jpip.service.js showProcessInfo', showProcessInfo);
            return this.serviceRunning;
        } // End: this.getJpipStream = function( file, entry, projCode )


    } // End: function jpipService( $http )

}());
