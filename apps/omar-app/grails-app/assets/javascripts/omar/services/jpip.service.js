(function () {
    'use strict';
    angular
        .module('omarApp')
        .service('jpipService', [ '$http', jpipService ]);
    // .service('jpipService', [jpipService]);    

    function jpipService( $http ) {

       var TRACE = 0;
       
       this.getJpipStream = function( $event, file, entry, projCode ) {

          if ( TRACE ) {
             console.log('jpipService.getJpipStream entered...');
             console.log('file:  ' + file);
             console.log('entry: ' + entry);
             console.log('projCode: ' + projCode );
          }
          
          var jpipAppEnabled = AppO2.APP_CONFIG.params.jpipApp.enabled;

          if ( TRACE ) {
             console.log( 'jpipAppEnabled: ' + jpipAppEnabled );
          }

          if ( jpipAppEnabled ) {
             var jpipLink = AppO2.APP_CONFIG.params.jpipApp.baseUrl;

             // projCode can be: chip, geo-scaled, 4326, 3857
             var jpipServiceUrl = jpipLink + '/createStream?filename=' + file + '&entry=' + entry + '&projCode=' + projCode;

             if ( TRACE ) {
                console.log( 'jpipServiceUrl: ' +  jpipServiceUrl );
             }

             // Poll service until we get a finished status.
             var MAX=240;
             var secondsEllapsed = 0;

             // Change the caller background color.
             // var bgColorSave = $event.currentTarget.style.backgroundColor;
             $event.currentTarget.style.backgroundColor = 'red';
             
             var timerId = setInterval( function() {
                $http({ method: 'GET', url: jpipServiceUrl }).then(function(response) {

                if ( TRACE ) {
                   var data = JSON.stringify(response.data);
                   console.log('response data', data);
                }
                
                if ( response.data.status == "FINISHED" ) {
                   if ( TRACE ) {
                      console.log('FINISHED...');
                   }
                   clearInterval(timerId);
                   $event.currentTarget.style.backgroundColor = 'green';
                   // $event.currentTarget.style.backgroundColor = bgColorSave;
                }
                else if ( secondsEllapsed > MAX ) {
                   
                   console.log('TODO put timeout code here...');
                }
                
                   }, function error(response) {
                      console.log( JSON.stringify(response) );
                      console.log('failed', response); // supposed to have: data, status, headers, config, statusText
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
          
       } // End: this.getJpipStream = function( file, entry, projCode )
       
    } // End: function jpipService( $http )
    
}());


