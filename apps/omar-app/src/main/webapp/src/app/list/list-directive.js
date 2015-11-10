/**
 * Created by adrake on 10/19/15.
 */
'use strict';
angular
    .module('omarApp')
    .directive('listImage', imageList);

    function imageList() {
       return {

        restrict: 'A',
        transclude: true,
        scope: {
            image: '=',
            omar: '@'
        },
        replace: true,
        templateUrl: 'list/list-partial.html'

       };
    }