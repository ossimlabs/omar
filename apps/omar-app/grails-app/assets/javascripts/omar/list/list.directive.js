(function(){
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

})();