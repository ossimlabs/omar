/**
 * Created by adrake on 10/19/15.
 */
'use strict';
angular
    .module('omarApp')
    .directive('imageList', imageList)

    function imageList() {
       return {

        restrict: 'A', // use both attribute and element
        scope: {
            image: '=' // = provides two-way data binding
        },
        replace: true,
        templateUrl: 'image-template.html'

       };
    };