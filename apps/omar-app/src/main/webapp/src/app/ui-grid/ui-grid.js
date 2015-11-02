var app = angular.module('app', ['ui.grid', 'ui.grid.resizeColumns']);

app.controller('MainCtrl', ['$scope', '$log', '$q', function ($scope, $log, $q) {

    var wfsClient;
    wfsClient = new OGC.WFS.Client('http://localhost:7272/wfs');

    OpenLayers.ProxyHost = "/proxy/index?url="

    var deferred = $q.defer();

    var wfsRequest = {
        typeName: 'omar:raster_entry',
        namespace: 'http://omar.ossim.org',
        version: '1.1.0',
        maxFeatures: 50,
        outputFormat: 'JSON'
    };

    if($scope.filter && $scope.filter.trim() !== ''){
        wfsRequest.cql = $scope.filter;
    }

    wfsClient.getFeature(wfsRequest, function(data) {
        deferred.resolve(data);
    });

    var promise = deferred.promise;
    promise.then(function(data){

        $scope.features = data;
        $log.warn('$scope.features', $scope.features.length);
        $scope.showGetFeatureTable = true;

        //var array = [];

        //for (var i=0; i < $scope.features.length; i++){
        //    array.push($scope.features[i].properties);
        //    console.log($scope.features[i].properties);
        //}

        var array = $scope.features.map(function(it){
            return it.properties;
        })

        //console.log('array', array);
        $scope.myData = array;

    });
    //$scope.gridOptions ={
    //    enableFiltering: true
    //}
    //$scope.myData = [
    //    {
    //        "firstName": "Cox",
    //        "lastName": "Carney",
    //        "company": "Enormo",
    //        "employed": true
    //    },
    //    {
    //        "firstName": "Lorraine",
    //        "lastName": "Wise",
    //        "company": "Comveyer",
    //        "employed": false
    //    },
    //    {
    //        "firstName": "Nancy",
    //        "lastName": "Waters",
    //        "company": "Fuelton",
    //        "employed": false
    //    }
    //];


}]);
