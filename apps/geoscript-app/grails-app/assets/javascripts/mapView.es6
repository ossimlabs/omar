//= require mapWidget.es6
//= require_self

const MapView = (function() {

  let mapWidget;

  function init(params){

    mapWidget = new MapWidget(params)

  }

  return {
    init: init,
  };

})();
