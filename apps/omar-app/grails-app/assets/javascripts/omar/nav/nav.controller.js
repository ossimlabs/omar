(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('NavController', ['stateService', NavController]);

        function NavController(stateService) {

        	// #################################################################################
            // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
            // provides access to various client params in application.yml
            // #################################################################################
            //console.log('AppO2.APP_CONFIG in NavController: ', AppO2.APP_CONFIG);

            var vm = this;
        	/* jshint validthis: true */

            stateService.navStateUpdate = function() {
                vm.titleLeft = stateService.navState.titleLeft;
            }

        	vm.swipeAppEnabled = AppO2.APP_CONFIG.params.swipeApp.enabled;

        	if (vm.swipeAppEnabled) {
        		vm.swipeAppLink = AppO2.APP_CONFIG.params.swipeApp.baseUrl;
        	}

        	vm.piwikAppEnabled = AppO2.APP_CONFIG.params.piwikApp.enabled;
        	if (vm.piwikAppEnabled) {
        		vm.piwikAppLink = AppO2.APP_CONFIG.params.piwikApp.baseUrl;
        	}

          vm.wmtsAppEnabled = AppO2.APP_CONFIG.params.wmtsApp.enabled;
        	if (vm.wmtsAppEnabled) {
        		vm.wmtsAppLink = AppO2.APP_CONFIG.params.wmtsApp.baseUrl;
        	}

        	vm.apiAppEnabled = AppO2.APP_CONFIG.params.apiApp.enabled;
        	if (vm.apiAppEnabled) {
        		vm.apiAppLink = AppO2.APP_CONFIG.params.apiApp.baseUrl;
        	}
        }
})();
