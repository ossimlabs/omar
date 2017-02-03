(function(){
  'use strict';
  angular
    .module('omarApp')
    .service('wfsService', ['$rootScope', '$http', '$timeout', wfsService]);

  function wfsService($rootScope, $http, $timeout) {

    // #################################################################################
    // AppO2.APP_CONFIG is passed down from the .gsp, and is a global variable.  It
    // provides access to various client params in application.yml
    // #################################################################################
    //console.log('AppO2.APP_CONFIG in wfsService: ', AppO2.APP_CONFIG);

    var wfsRequestUrl = AppO2.APP_CONFIG.params.wfs.baseUrl;
    var wfsRequest = {
        typeName: 'omar:raster_entry',
        namespace: 'http://omar.ossim.org',
        version: '1.1.0',
        outputFormat: 'JSON',
        cql: '',
        sortField: 'acquisition_date',
        sortType: '+D',
        startIndex: '0',
        maxFeatures: '1000'
    };

    // When this changes it needs to be passed to the executeWfsQuery method
    this.spatialObj = {
      filter: ""
    };

    // When this changes it needs to be passed to the executeWfsQuery method
    this.attrObj = {
      filter: "",
      sortField: "acquisition_date",
      sortType: "+D",
      startIndex: 0,
      pageLimit: 10
    };

    if (AppO2.APP_CONFIG.params.misc.pageLimit != undefined) {
      this.attrObj.pageLimit = AppO2.APP_CONFIG.params.misc.pageLimit;
    }

    this.updateSpatialFilter = function(filter) {

      this.spatialObj.filter = filter;
      $rootScope.$broadcast(
          'spatialObj.updated', filter
      );

    };

    this.updateAttrFilter = function(filter, sortField, sortType) {

      this.attrObj.filter = filter;

      if (sortField !== undefined) {

        this.attrObj.sortField = sortField;

      }

      if (sortType !== undefined) {

        this.attrObj.sortType = sortType;

      }

      $rootScope.$broadcast(

        'attrObj.updated', filter

      );

    };

    this.updateAttrFilterPaginate = function(startIndex){

      var boolUpdate = false;
      if (startIndex !== undefined){
        if (this.attrObj.startIndex != startIndex){
          boolUpdate = true;
          this.attrObj.startIndex = startIndex;
        }
      }
      if(boolUpdate){
        $rootScope.$broadcast(
          'attrObj.updated', this.attrObj.filter
        );
      }
    }

    this.executeWfsQuery = function() {

      if (this.attrObj.filter === "") {

        // Only send the spatialObj to filter the results
        wfsRequest.cql = this.spatialObj.filter;

      } else if (this.spatialObj.filter === "") {

        // Only send the attrObj to filter the results
        wfsRequest.cql = this.attrObj.filter;

      } else {

        // Filter the results using the spatialObj and the attrObj
        wfsRequest.cql = this.spatialObj.filter + " AND " + this.attrObj.filter;

      }

      wfsRequest.sortField = this.attrObj.sortField;
      wfsRequest.sortType = this.attrObj.sortType;
      wfsRequest.startIndex = this.attrObj.startIndex;
      wfsRequest.pageLimit = this.attrObj.pageLimit;

      var wfsUrl = wfsRequestUrl +
        "service=WFS" +
        "&version=" + wfsRequest.version +
        "&request=GetFeature" +
        "&typeName=" + wfsRequest.typeName +
        "&filter=" + encodeURIComponent(wfsRequest.cql) +
        "&outputFormat=" + wfsRequest.outputFormat +
        "&sortBy=" + wfsRequest.sortField + wfsRequest.sortType +
        "&startIndex=" + wfsRequest.startIndex +
        "&maxFeatures=" +  wfsRequest.pageLimit; // wfsRequest.maxFeatures;

      $http({
        method: 'GET',
        url: wfsUrl
      })
      .then(function (response) {
        var data;
        data = response.data.features;

        // $timeout needed: http://stackoverflow.com/a/18996042
        $timeout(function () {
            $rootScope.$broadcast('wfs: updated', data);
          });
      });

      // http://o2.cloudapps.ossimc2s.com/o2-wfs/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName=omar:raster_entry&outputFormat=json&resultType=hits
      // TODO: append &resultType=hits to the url, and get a count of the images in the current filter

      var wfsFeaturesUrl = wfsRequestUrl +
        "service=WFS" +
        "&version=" + wfsRequest.version +
        "&request=GetFeature" +
        "&typeName=" + wfsRequest.typeName +
        "&filter=" + encodeURIComponent(wfsRequest.cql) +
        "&outputFormat=" + wfsRequest.outputFormat +
        "&sortBy=" + wfsRequest.sortField + wfsRequest.sortType +
        "&startIndex=" + wfsRequest.startIndex +
        "&resultType=hits"

      $http({
        method: 'GET',
        url: wfsFeaturesUrl
      })
      .then(function (response) {
        var features;
        features = response.data.totalFeatures;

        // $timeout needed: http://stackoverflow.com/a/18996042
        $timeout(function () {
            $rootScope.$broadcast('wfs features: updated', features);
          });
      });

    };

  }

}());
