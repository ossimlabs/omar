(function(){

    'use strict';
    // https://github.com/philippd/angular-deferred-bootstrap
    deferredBootstrapper.bootstrap({
        element: document.body,
        module: 'omarApp',
        resolve: {
            APP_CONFIG: ['$http', function ($http) {
                //return $http.get('../config.json');
                return $http.get('/webAppConfig');
            }]
        }
    });

    angular
        .module('omarApp',['ui.router'])
        .config(function($stateProvider, $urlRouterProvider)  {

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


})();