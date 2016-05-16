(function () {
    'use strict';
    angular
        .module('omarApp')
        .service('jpipService', [ '$http', jpipService ]);
    // .service('jpipService', [jpipService]);    

    function jpipService( $http ) {

       var TRACE = true;
       
       this.getJpipStream = function( file, entry, projCode ) {

          if ( Boolean(TRACE) )
          {
             console.log('jpipService.getJpipStream entered...');
             console.log('file:  ' + file);
             console.log('entry: ' + entry);
             console.log('projCode: ' + projCode );
          }
          
          var jpipAppEnabled = AppO2.APP_CONFIG.params.jpipApp.enabled;

          if ( Boolean(TRACE) )
          {
             console.log( 'jpipAppEnabled' + jpipAppEnabled );
          }

          if ( jpipAppEnabled )
          {
             var jpipLink = AppO2.APP_CONFIG.params.jpipApp.baseUrl;

             // projCode can be: chip, geo-scaled, 4326, 3857
             var jpipServiceUrl = jpipLink + '/createStream?filename=' + file + '&entry=' + entry + '&projCode=' + projCode;

             if ( Boolean(TRACE) )
             {
                console.log( 'jpipServiceUrl: ' +  jpipServiceUrl );
             }

             $http({ method: 'GET', url: jpipServiceUrl }).then(function(response) {
                   
                   var data;
                   data = response;  // callback response from Predictive IO controller
                   console.log('rating response', data);
                   
                },
                function error(response) {
                   console.log( JSON.stringify(response) );
                   
                   console.log('failed', response); // supposed to have: data, status, headers, config, statusText
                   
                });             
          }
          
          console.log('jpipServie.getJpipStream exited...');
          
       };
        
    }

}());


