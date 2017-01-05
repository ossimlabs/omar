(function(){
  'use strict';
  angular
    .module('omarApp')
    .controller('NavController', ['$scope', NavController]);

  function NavController($scope) {

    // #################################################################################
    // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
    // provides access to various client params in application.yml
    // #################################################################################
    //console.log('AppO2.APP_CONFIG in NavController: ', AppO2.APP_CONFIG);

      var vm = this;
      /* jshint validthis: true */

    $scope.$on('navState.updated', function(event, params) {
      vm.titleLeft = params.titleLeft;

      if ( params.userGuideUrl && vm.userGuideEnabled ) { 
        var base = AppO2.APP_CONFIG.params.userGuide.baseUrl;
        vm.userGuideLink = base + params.userGuideUrl;
      }
    });

    vm.apiAppEnabled = AppO2.APP_CONFIG.params.apiApp.enabled;
    if (vm.apiAppEnabled) {
      vm.apiAppLink = AppO2.APP_CONFIG.params.apiApp.baseUrl;
    }

    vm.piwikAppEnabled = AppO2.APP_CONFIG.params.piwikApp.enabled;
    if (vm.piwikAppEnabled) {
      vm.piwikAppLink = AppO2.APP_CONFIG.params.piwikApp.baseUrl;
    }

    vm.kmlAppEnabled = AppO2.APP_CONFIG.params.kmlApp.enabled;
    if (vm.kmlAppEnabled) {
      vm.kmlAppLink = AppO2.APP_CONFIG.params.kmlApp.baseUrl + "/superOverlay/getLastImagesKml";
    }

    vm.tlvAppEnabled = AppO2.APP_CONFIG.params.tlvApp.enabled;
    if (vm.tlvAppEnabled) {
      vm.tlvAppLink = AppO2.APP_CONFIG.params.tlvApp.baseUrl;
    }

    vm.userGuideEnabled = AppO2.APP_CONFIG.params.userGuide.enabled;
    if (vm.userGuideEnabled) {
      vm.userGuideLink = AppO2.APP_CONFIG.params.userGuide.baseUrl;
    }
  }
})();
