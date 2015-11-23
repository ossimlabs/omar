/**
 * Created by adrake on 11/23/15.
 */
// http://jsbin.com/suxed/1/edit?html,js,output
// http://stackoverflow.com/questions/21919962/share-data-between-angularjs-controllers

(function () {

    'use strict';
    angular
        .module('omarApp')
        .service('multipleService', multipleService);

    function multipleService(){


        // private variable
        var _dataObj = {};

        this.dataObj = _dataObj;

    }

}());
