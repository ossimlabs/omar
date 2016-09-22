(function () {
  'use strict';

  angular
    .module('omarApp', [
      'ui.router',
      'ui.bootstrap',
      'angularSpinner',
      'toastr',
      'mgcrea.ngStrap',
      'slickCarousel',
      'toggle-switch',
      'angular-clipboard'])
    .config(['$stateProvider', '$urlRouterProvider',

      function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/home');

        $stateProvider
                .state('home', {
                  url: '/home',
                  templateUrl:  AppO2.APP_CONFIG.serverURL + '/home/home.partial.html'
                })
                .state('map', {
                  url: '/map/?&cql',
                  templateUrl: AppO2.APP_CONFIG.serverURL + '/map/map.partial.html',
                  params: {
                    mapParams: {
                      value: 'mapParamsDefaultMap',
                      squash: true
                    },
                  },
                })
                .state('mapOrtho', {
                  url: '/mapOrtho?layers',
                  templateUrl: AppO2.APP_CONFIG.serverURL + '/mapOrtho/map.ortho.partial.html'
                })
                .state('mapImage', {
                  url: '/mapImage?filename=&entry_id=&width=&height',
                  templateUrl: AppO2.APP_CONFIG.serverURL + '/mapImage/map.image.partial.html'
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
        .filter('fileNameTrim', function () {
              return function (name) {
                if (name !== undefined) {
                  var filename = name.replace(/^.*[\\\/]/, '');
                  return filename;
                }
              };
            });

})();
