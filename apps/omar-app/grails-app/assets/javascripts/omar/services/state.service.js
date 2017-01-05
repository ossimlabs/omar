(function() {
'use strict';
angular
  .module('omarApp')
  .service('stateService', ['$rootScope','$timeout', stateService]);

  function stateService($rootScope, $timeout) {

    var _this = this;

    var mapState = {
      center: {
        lat: 0,
        lng: 0
      },
      zoom: 3,
      feature: {
        lat: undefined,
        lng: undefined,
        wkt: undefined,
        bounds: {
          ne: {
            lat: undefined,
            lng: undefined
          },
          sw: {
            lat: undefined,
            lng: undefined
          }
        }
      }
    }

    _this.mapState = mapState;

    _this.navState = {};
    _this.navStateUpdate = function(object) {
        _this.navState.titleLeft = object.titleLeft || "";
        _this.navState.userGuideUrl = object.userGuideUrl || null;

        $rootScope.$broadcast('navState.updated', _this.navState);
    }

    _this.updateMapState = function(objParams) {

      _this.mapState.center.lat = objParams.lat;
      _this.mapState.center.lng = objParams.lng;
      _this.mapState.feature.lat = objParams.lat;
      _this.mapState.feature.lng = objParams.lng;

      // Assign these values to the mapState object only if
      // twofishes has a wkt value that is defined.
      if (objParams.wkt !== undefined) {

        _this.mapState.feature.wkt = objParams.wkt;
        _this.mapState.feature.bounds.ne.lat = objParams.bounds.ne.lat;
        _this.mapState.feature.bounds.ne.lng = objParams.bounds.ne.lng;
        _this.mapState.feature.bounds.sw.lat = objParams.bounds.sw.lat;
        _this.mapState.feature.bounds.sw.lng = objParams.bounds.sw.lng;

      }

      $rootScope.$broadcast(
          'mapState.updated', _this.mapState
      );

    }

    _this.resetFeatureMapState = function() {

      _this.mapState.feature.lat = undefined;
      _this.mapState.feature.lng = undefined;
      _this.mapState.feature.wkt = undefined;
      _this.mapState.feature.bounds.ne.lat = undefined;
      _this.mapState.feature.bounds.ne.lng = undefined;
      _this.mapState.feature.bounds.sw.lat = undefined;
      _this.mapState.feature.bounds.sw.lng = undefined;

    }

  }

}());
