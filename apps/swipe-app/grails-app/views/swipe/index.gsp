<!DOCTYPE html>
<html>
  <head>
    <title>Single Image WMS</title>

    <asset:stylesheet src="webjars/openlayers/3.13.0/ol.css"></asset:stylesheet>
    <asset:javascript src="webjars/openlayers/3.13.0/ol.js"></asset:javascript>
    <asset:javascript src="webjars/angularjs/1.4.8/angular.js"></asset:javascript>



  </head>
  <body ng-app="swipe">
    <div id="map" class="map"></div>
    <input id="swipe" type="range" style="width: 100%">

    <div ng-app="swipe" ng-controller="SwipeController as swipe">
      <b>URL:</b> <input type="text" ng-model="swipe.url"><br>

      <b>Layer 1:</b> <input type="text" ng-model="swipe.layer1">
      <button class="btn" ng-click="swipe.addLayer1(swipe.layer1)">add</button>
      <button class="btn" ng-click="swipe.removeLayer1(swipe.layer1)">remove</button><br>

      <b>Layer 2:</b> <input type="text" ng-model="swipe.layer2">
      <button class="btn" ng-click="swipe.addLayer2(swipe.layer2)">add</button>
      <button class="btn" ng-click="swipe.removeLayer2(swipe.layer2)">remove</button><br>

      <button class="btn" ng-click="swipe.swap(swipe.layer1, swipe.layer2)">swap</button>

  </div>







<script>
angular.module('swipe', [])
.controller('SwipeController', function() {
  this.url = 'http://omar.ossim.org/ogc/wms?';
  this.layer1 = 351;
  this.layer2 = 349;

  this.swap = function swap(layer1, layer2) {
    var layers = [layer1, layer2];

    removeLayer1(layer1);
    removeLayer2(layer2);

    this.layer1 = layers[1];
    this.layer2 = layers[0];

addLayer1(layers[1]);
addLayer2(layers[0]);


  }

  this.addLayer1 = function add1(layer) {
    addLayer1(layer);
  };
  this.removeLayer1 = function remove1(layer) {
    removeLayer1(layer);
  };

  this.addLayer2 = function add2(layer) {
    addLayer2(layer);
  };
  this.removeLayer2 = function remove2(layer) {
    removeLayer2(layer);
  }; 



  

  /*this.total = function total(outCurr) {
    return this.convertCurrency(this.qty * this.cost, this.inCurr, outCurr);
  };
  this.convertCurrency = function convertCurrency(amount, inCurr, outCurr) {
    return amount * this.usdToForeignRates[outCurr] / this.usdToForeignRates[inCurr];
  };
  this.pay = function pay() {
    window.alert("Thanks!");
  };*/
});
  




 var osm = new ol.layer.Tile({
        source: new ol.source.OSM()
      });


      


      
      var map = new ol.Map({
        layers: [osm],
        target: 'map',
        view: new ol.View({
          center: [-10997148, 4569099],
          zoom: 4
        })
      });



var omar;
function addLayer1(i)
{
  omar = new ol.layer.Tile({
    opacity: .5,
    source: new ol.source.TileWMS({
      url: 'http://omar.ossim.org/omar/ogc/wms?',
      params: {
        'LAYERS': i,
        'TILED': true,
        'VERSION': '1.1.1'
      }
    })
  });

map.addLayer(omar);
}
function removeLayer1(i)
{
  map.removeLayer(omar);
}


var omar2;
function addLayer2(i)
{
  omar2 = new ol.layer.Tile({
    opacity: .7,
    source: new ol.source.TileWMS({
      url: 'http://omar.ossim.org/omar/ogc/wms?',
      params: {
        'LAYERS': i,
        'TILED': true,
        'VERSION': '1.1.1'
      }
    })
  });


map.addLayer(omar2);
setSwipe();
}
function removeLayer2(i)
{


map.removeLayer(omar2);

}


      var swipe = document.getElementById('swipe');





function setSwipe() {

      omar2.on('precompose', function(event) {
        var ctx = event.context;
        var width = ctx.canvas.width * (swipe.value / 100);

        ctx.save();
        ctx.beginPath();
        ctx.rect(width, 0, ctx.canvas.width - width, ctx.canvas.height);
        ctx.clip();
      });

      omar2.on('postcompose', function(event) {
        var ctx = event.context;
        ctx.restore();
      });

      swipe.addEventListener('input', function() {
        map.render();
      }, false);

};


      //////////
/*



*/
    </script>
  </body>
</html>