/**
 * Created by adrake on 11/23/15.
 */

(function () {
    'use strict';

    angular
        .module('omarApp')
        .controller('MultipleController1', MultipleController1)
        .controller('MultipleController2', MultipleController2)
        .controller('MultipleController3', MultipleController3)
        .controller('MultipleController4', MultipleController4);

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