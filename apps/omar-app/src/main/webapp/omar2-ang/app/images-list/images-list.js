omarApp.controller('ImagesListCtrl', ['$scope', '$http',

	function ($scope, $http) {

		$scope.endPoint = 'http://demo.boundlessgeo.com/geoserver/wfs';

		function formatHeader(fieldName){
			return fieldName.toLowerCase().split('_').map(function(it){
				return it.charAt(0).toUpperCase() + it.slice(1);
			}).join(' ');
		}

		function createHeader(fieldName){
			return {headerName: formatHeader(fieldName), field: fieldName};
		}
	
		$scope.init = function (typeName){
			$scope.typeName = typeName;
			$scope.requestSchema = '?service=WFS&version=1.1.0&request=DescribeFeatureType&typeName=' + typeName;
			$scope.requestItems = '?service=WFS&version=1.1.0&request=GetFeature&outputFormat=JSON&typeName=' + $scope.typeName;
			$http.get($scope.endPoint + $scope.requestSchema)
				.success(
					function(data){
						
						var format = new OpenLayers.Format.WFSDescribeFeatureType();
						
						$scope.schema = (format.read(data).featureTypes[0].properties).map(function(it){
							return createHeader(it.name);
						});
						console.log('schema', $scope.schema);

				});
			$scope.fetch();

		}

		$scope.fetch = function (){

			$scope.orderProp = 'id';

			return $http.get($scope.endPoint + $scope.requestItems)
				.success(
					function(data){
						$scope.states = data.features;
						console.log('states', $scope.states);
				});

		}

		$scope.init('topp:states');

}]);
