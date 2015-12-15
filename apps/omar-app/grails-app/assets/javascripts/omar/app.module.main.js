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
        .module('omarApp', ['ui.router', 'ui.bootstrap', 'angularSpinner', 'toastr'])
        .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.otherwise('/home');

            $stateProvider
                // HOME STATES AND NESTED VIEWS ========================================

                .state('home', {
                    url: '/home',
                   // templateUrl: '../src/app/home/home.partial.html'
                    templateUrl: '/home/home.partial.html'
                })

                .state('map', {

                    url: '/map/?&cql',
                    templateUrl: '/map/map.partial.html',
                    params: {
                        //param1: "defaultValue"
                        mapParams: {
                            value: 'mapParamsDefaultMap',
                            squash: true
                        }
                    }

                })

                .state('wfs', {
                    url: '/wfs',
                    templateUrl: '/wfs/wfs.partial.html'
                })

                .state('multiple', {
                    url: '/multiple',
                    templateUrl: '/multiple/multiple.partial.html'
                });

        }]); // closes $routerApp.config()

})();