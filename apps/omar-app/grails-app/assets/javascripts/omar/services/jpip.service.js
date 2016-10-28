(function() {

    'use strict';
    angular
        .module('omarApp')
        .service('jpipService', ['$rootScope', '$http', 'toastr', '$timeout', 'shareService', jpipService]);

    function jpipService($rootScope, $http, toastr, $timeout, shareService) {
        var TRACE = 0;
        this.serviceRunning = true;

        this.getJpipStream = function($event, file, entry, projCode) {
                var jpipAppEnabled = AppO2.APP_CONFIG.params.jpipApp.enabled;
                var jpipLink = "";
                var jpipServiceUrl = "";
                var MAX = 240;
                var secondsEllapsed = 0;
                var timerId = 0;
                var f = ""; // To hold basename of image file.

                if (TRACE) {
                    console.log('jpipService.getJpipStream entered...');
                    console.log('jpipAppEnabled: ' + jpipAppEnabled);
                    console.log('file:  ' + file);
                    console.log('entry: ' + entry);
                    console.log('projCode: ' + projCode);
                }

                if (jpipAppEnabled) {
                    // Get the basename of the image file for the toastr popup:
                    f = file.replace(/^.*[\\\/]/, '');

                    jpipLink = AppO2.APP_CONFIG.params.jpipApp.baseUrl;

                    // projCode can be: chip, geo-scaled, 4326, 3857
                    jpipServiceUrl = jpipLink + '/createStream?filename=' + file + '&entry=' + entry + '&projCode=' + projCode;

                    if (TRACE) {
                        console.log('jpipServiceUrl: ' + jpipServiceUrl);
                    }

                    // Change the opacity of the anchor/button to look greyed out.
                    $event.currentTarget.style.opacity = 0.4;

                    // Clear any previous instance of timerId
                    clearInterval(timerId);

                    // Poll service until we get a finished status.
                    timerId = setInterval(function() {

                        $http({
                            method: 'GET',
                            url: jpipServiceUrl + '&' + new Date()
                        }).then(function(response) {

                            if (TRACE) {
                                data = JSON.stringify(response.data);
                                console.log('response data', JSON.stringify(response.data));
                            }

                            if (response.data.status === "FINISHED") {
                                if (TRACE) {
                                    console.log('FINISHED...');
                                }

                                clearInterval(timerId);

                                $event.currentTarget.style.opacity = 1.0;

                                shareService.imageLinkModal(response.data.url, 'JPIP Stream URL Link');

                                $timeout(function() {
                                    $rootScope.$broadcast('jpip: updated');
                                });

                            } else if (secondsEllapsed > MAX) {

                                toastr.error("Sorry: JPIP steam conversion hit time!",
                                    "File: " + f, {
                                        positionClass: 'toast-bottom-left',
                                        closeButton: true,
                                        timeOut: 10000,
                                        extendedTimeOut: 5000,
                                        target: 'body',
                                        preventDuplicates: true,
                                        preventOpenDuplicates: true,

                                    });

                                clearInterval(timerId);

                                $timeout(function() {
                                    $rootScope.$broadcast('jpip: updated');
                                });

                            }

                        }, function error(response) {

                            console.log(JSON.stringify(response));
                            console.log('failed', response);

                        });
                        secondsEllapsed += 2;

                        if (TRACE) {
                            console.log('secondsEllapsed: ' + secondsEllapsed);
                        }

                    }, 2000);

                    if (TRACE) {
                        console.log('jpipServie.getJpipStream exited...');
                    }

                } // if ( jpipAppEnabled )

                return this.serviceRunning;

            } // End: this.getJpipStream = function( file, entry, projCode )


    } // End: function jpipService( $http )

}());
