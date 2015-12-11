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
        .module('omarApp', ['ui.router', 'ui.bootstrap', 'angularSpinner'])
        .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

            //$urlRouterProvider.otherwise('/home');

            $stateProvider
                // HOME STATES AND NESTED VIEWS ========================================

                //http://localhost:7272/omar/index#/home
                .state('home', {
                    url: '/home',
                    templateUrl: 'src/app/home/home.partial.html'
                })

                .state('map', {

                    url: '/map/?&cql',
                    templateUrl: 'src/app/map/map.partial.html',
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
                    templateUrl: 'src/app/wfs/wfs.partial.html'
                })

                .state('multiple', {
                    url: '/multiple',
                    templateUrl: 'src/app/multiple/multiple.partial.html'
                });

        }]); // closes $routerApp.config()

})();