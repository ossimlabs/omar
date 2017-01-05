(function () {
  'use strict';
  angular
    .module('omarApp')
    .controller('HomeController', ['stateService', '$window', 'toastr', HomeController]);

  function HomeController(stateService, $window, toastr){

    // #################################################################################
    // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
    // provides access to various client params in application.yml
    // #################################################################################
    //console.log('AppO2.APP_CONFIG in HomeController: ', AppO2.APP_CONFIG);

    // set header title
    stateService.navStateUpdate({
        titleLeft: "<h3>Welcome!</h3>",
        userGuideUrl: " "
    });

    /* jshint validthis: true */
    var vm = this;

    vm.title = 'Image Discovery and Analysis';
    vm.titleMessage = 'Find by place name or coordinates';

    vm.baseUrl = AppO2.APP_CONFIG.serverURL;

    vm.apiAppEnabled = AppO2.APP_CONFIG.params.apiApp.enabled;
    if (vm.apiAppEnabled) {
        vm.apiAppLink = AppO2.APP_CONFIG.params.apiApp.baseUrl;
    }

    vm.kmlAppEnabled = AppO2.APP_CONFIG.params.kmlApp.enabled;
    if (vm.kmlAppEnabled) {
        vm.kmlAppLink = AppO2.APP_CONFIG.params.kmlApp.baseUrl + "/superOverlay/getLastImagesKml";
    }

    vm.piwikAppEnabled = AppO2.APP_CONFIG.params.piwikApp.enabled;
    if (vm.piwikAppEnabled) {
        vm.piwikAppLink = AppO2.APP_CONFIG.params.piwikApp.baseUrl;
    }

    vm.tlvAppEnabled = AppO2.APP_CONFIG.params.tlvApp.enabled;
    if (vm.tlvAppEnabled) {
        vm.tlvAppLink = AppO2.APP_CONFIG.params.tlvApp.baseUrl;
    }

    var twofishProxy = AppO2.APP_CONFIG.params.twofishes.proxy;

    vm.go = function (url) {

      console.log(url);

      $window.open(url);

    }

}

})();
