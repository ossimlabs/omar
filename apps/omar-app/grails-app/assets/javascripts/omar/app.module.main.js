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

    // console.log('AppO2 in module main', AppO2);
    // console.log('location: ', window.location);

    //var urlPrefix = AppO2;


    angular
        .module('omarApp', [
            'ui.router',
            'ui.bootstrap',
            'angularSpinner',
            'toastr',
            'mgcrea.ngStrap.timepicker',
            'slickCarousel',
            'toggle-switch',
            'angular-clipboard'])
        .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.otherwise('/home');

            $stateProvider
                // HOME STATES AND NESTED VIEWS ========================================

                .state('home', {
                    //url: '/o2/home',
                    url: '/home',
                    // templateUrl: '../src/app/home/home.partial.html'
                    //templateUrl: '/o2/home/home.partial.html'

                    templateUrl:  AppO2.APP_CONFIG.serverURL + '/home/home.partial.html'

                })

                .state('map', {

                    url: '/map/?&cql',
                    templateUrl: AppO2.APP_CONFIG.serverURL + '/map/map.partial.html',
                    params: {
                        //param1: "defaultValue"
                        mapParams: {
                            value: 'mapParamsDefaultMap',
                            squash: true
                        }
                    }

                })

                .state('mapOrtho', {
                    url: '/mapOrtho?layers',
                    templateUrl: AppO2.APP_CONFIG.serverURL + '/mapOrtho/map.ortho.partial.html'
                })

                .state('wfs', {
                    url: '/wfs',
                    templateUrl: AppO2.APP_CONFIG.serverURL + '/wfs/wfs.partial.html'
                })

                .state('multiple', {
                    url: '/o2/multiple',
                    templateUrl: AppO2.APP_CONFIG.serverURL + '/multiple/multiple.partial.html'
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
