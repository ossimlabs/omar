(function(){
    'use strict';
    angular
        .module('omarApp')
        .controller('MultipleController1', ['multipleService', MultipleController1])
        .controller('MultipleController2', ['multipleService', MultipleController2])
        .controller('MultipleController3', ['multipleService', MultipleController3])
        .controller('MultipleController4', ['multipleService', MultipleController4]);

        function MultipleController1(multipleService){
            var vm = this;

            vm.data = multipleService.dataObj;

        }

        function MultipleController2(multipleService){
            var vm = this;

            vm.data = multipleService.dataObj;

        }

        function MultipleController3(multipleService){
            var vm = this;

            vm.data = multipleService.dataObj;

        }

        function MultipleController4(multipleService){
            var vm = this;

            vm.data = multipleService.dataObj;

        }

}());