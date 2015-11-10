(function(){
    // https://blog.mariusschulz.com/2014/10/22/asynchronously-bootstrapping-angularjs-applications-with-server-side-data
    // The boostrapping of the AngularJS application entirely depends on the AJAX request
    // being successful. If the request fails, the application won't be initialized at all.
    //  We should consider this and implement a retry mechanism or provide some default
    // data in case of a loading error.
    // TODO: Create a retry mechanism and default data in case of a loading error
    'use strict';
    var omarApp = angular.module("omarApp", ['ui.router'])
        // TODO: Move .config items to separate app.config.js file
        .config(function($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.otherwise('/home');

            $stateProvider
                // HOME STATES AND NESTED VIEWS ========================================
                .state('home', {
                    url: '/home',
                    templateUrl: 'home/home.partial.html'
                })

                .state('map', {

                    url: '/map/?&cql',
                    templateUrl: 'map/map.partial.html',
                    params: {
                        //param1: "defaultValue"
                        mapParams: {
                            value: 'mapParamsDefaultValue',
                            squash: true
                        }
                    }

                });
        }); // closes $routerApp.config()

    fetchData().then(bootstrapApplication());

    function fetchData(){
        var initInjector = angular.injector(['ng']);
        var $http = initInjector.get("$http");

        return $http({
            url: '../config.json',
            method: 'GET'
        }).then(function(response){
            console.log(response.data);
            omarApp.constant("omarConfig", response.data);
            //angular.module('ng').constant("omarConfig", response.data);
        }, function(errorResponse){
            // Handle error case
        });

    }

    function bootstrapApplication(){
        angular.element(document).ready(function(){
           angular.bootstrap(document, ["omarApp"]);
        });
    }

})();