(function () {
  'use strict';
  angular
    .module('omarApp')
    .controller('HomeController', [/*'APP_CONFIG', */'$scope', '$state', 'stateService', 'wfsService', 'toastr', '$http', HomeController]);

  function HomeController(/*APP_CONFIG,*/ $scope, $state, stateService, wfsService, toastr, $http, $timeout){

    // #################################################################################
    // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
    // provides access to various client params in application.yml
    // #################################################################################
    //console.log('AppO2.APP_CONFIG in HomeController: ', AppO2.APP_CONFIG);

    // set header title
    stateService.navState.titleLeft = "<h3>Welcome!</h3>";
    stateService.navStateUpdate();

    /* jshint validthis: true */
    var vm = this;

    vm.title = 'Image Discovery and Analysis';
    vm.titleMessage = 'Find by place name or coordinates';

    vm.baseUrl = AppO2.APP_CONFIG.serverURL;

    vm.piwikAppEnabled = AppO2.APP_CONFIG.params.piwikApp.enabled;
    if (vm.piwikAppEnabled) {
        vm.piwikAppLink = AppO2.APP_CONFIG.params.piwikApp.baseUrl;
    }

    vm.apiAppEnabled = AppO2.APP_CONFIG.params.apiApp.enabled;
    if (vm.apiAppEnabled) {
        vm.apiAppLink = AppO2.APP_CONFIG.params.apiApp.baseUrl;
    }

    vm.tlvAppEnabled = AppO2.APP_CONFIG.params.tlvApp.enabled;
    if (vm.tlvAppEnabled) {
        vm.tlvAppLink = AppO2.APP_CONFIG.params.tlvApp.baseUrl;
    }

    var twofishProxy = AppO2.APP_CONFIG.params.twofishes.proxy;



}

})();
