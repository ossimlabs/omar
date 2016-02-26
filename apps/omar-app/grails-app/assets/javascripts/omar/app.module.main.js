(function(){
    'use strict';
    // //https://github.com/philippd/angular-deferred-bootstrap
    // deferredBootstrapper.bootstrap({
    //     element: document.body,
    //     module: 'omarApp',
    //     resolve: {
    //         APP_CONFIG: ['$http', function ($http) {
    //             //return $http.get('../config.json');
    //             return $http.get('/o2/webAppConfig');
    //         }]
    //     }
    // });

    angular
        .module('omarApp', [
            'ui.router',
            'ui.bootstrap',
            'angularSpinner',
            'toastr',
            'mgcrea.ngStrap.timepicker',
            'slickCarousel',
            'toggle-switch'])
        .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.otherwise('/o2/home');

            $stateProvider
                // HOME STATES AND NESTED VIEWS ========================================

                .state('home', {
                    url: '/o2/home',
                   // templateUrl: '../src/app/home/home.partial.html'
                    templateUrl: '/o2/home/home.partial.html'
                })

                .state('map', {

                    url: '/o2/map/?&cql',
                    templateUrl: '/o2/map/map.partial.html',
                    params: {
                        //param1: "defaultValue"
                        mapParams: {
                            value: 'mapParamsDefaultMap',
                            squash: true
                        }
                    }

                })

                .state('mapOrtho', {
                    url: '/o2/mapOrtho?layers',
                    templateUrl: '/o2/mapOrtho/map.ortho.partial.html'
                })

                .state('wfs', {
                    url: '/o2/wfs',
                    templateUrl: '/o2/wfs/wfs.partial.html'
                })

                .state('multiple', {
                    url: '/o2/multiple',
                    templateUrl: '/o2/multiple/multiple.partial.html'
                });

        }])
        .filter('fileNameTrim', function() {
            return function(name) {
                if (name !== undefined) {
                    var filename = name.replace(/^.*[\\\/]/, '');
                    //console.log('filename', filename);
                    return filename;
                }
            };
        });

})();