(function(){
    'use strict';
    angular
        .module('omarApp')
        .service('multipleService', [multipleService]);

    function multipleService(){
        // http://jsbin.com/suxed/1/edit?html,js,output
        // http://stackoverflow.com/questions/21919962/share-data-between-angularjs-controllers

        // private variable
        var _dataObj = {};

        this.dataObj = _dataObj;

    }

}());
