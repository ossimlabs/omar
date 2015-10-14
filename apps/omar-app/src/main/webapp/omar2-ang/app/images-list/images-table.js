omarApp.controller('ImagesTableCtrl', ['$scope', '$http', function ($scope, $http) {

	$scope.endPoint = 'http://demo.boundlessgeo.com/geoserver/wfs';

	function formatHeaders (featureTypeSchema){ 
		// Functional progamming fun!
		featureTypeSchema.map(function(it){
			return it.name.toLowerCase();
		}).map(function(it){
			return it.split('_');
		}).map(function(it){
			return it.map(function(it){
				return it.charAt(0).toUpperCase() + it.slice(1);
			});
			//return it.toUpperCase();
		}).map(function(it){
			return it.join(' ');
		});
	};

	function formatHeader(fieldName){
		return fieldName.toLowerCase().split('_').map(function(it){
			return it.charAt(0).toUpperCase() + it.slice(1);
		}).join(' ');
	}

	function createHeader(fieldName){
		return {headerName: formatHeader(fieldName), field: fieldName};
	}

	$scope.requestSchema = '?service=WFS&version=1.1.0&request=DescribeFeatureType&typeName=' + 'topp:states';
	$scope.requestItems = '?service=WFS&version=1.1.0&request=GetFeature&outputFormat=JSON&typeName=' + 'topp:states';
	
	$scope.gridOptions = 
        {
            ready: function(api){ 
            	console.log('ready!');
            	get_data(api); 
            }
        };

        function get_data(api)
        {
        console.log('get_data firing!');
        $http.get($scope.endPoint + $scope.requestSchema)
            .then(
                function(res) 
                {
                    
                	var format = new OpenLayers.Format.WFSDescribeFeatureType();
                					
					$scope.schema = format.read(data)
					console.log('schema', $scope.schema);

                    console.log(res);
                    $scope.gridOptions.columnDefs = res.data.columnDefs;
                    $scope.gridOptions.rowData    = res.data.rowData;
                    //$scope.gridOptions.api.onNewCols();
                    //$scope.gridOptions.api.onNewRows();
                }
            );
        };
























	// $http.get($scope.endPoint + $scope.requestSchema)
	// 	.success(
	// 		function(data){
				
	// 			var format = new OpenLayers.Format.WFSDescribeFeatureType();
				
	// 			$scope.schema = (format.read(data).featureTypes[0].properties).map(function(it){
	// 				return createHeader(it.name);
	// 			});
	// 			console.log('schema', $scope.schema);

	// 	});

	// var columnDefs = [
	// 	{field: "make", headerName: "Make"},
	// 	{field: "model", headerName: "Model"},
	// 	{field: "price", headerName: "Price"}
	// ];
	// console.log('columnDefs', columnDefs);

	// var rowData = [
	//     {make: "Toyota", model: "Celica", price: 35000},
	//     {make: "Ford", model: "Mondeo", price: 32000},
	//     {make: "Porsche", model: "Boxter", price: 72000}
	// ];

	// $scope.gridOptions = {
	//     columnDefs: columnDefs,
	//     rowData: rowData
	// };

	// $scope.init = function (typeName){
		
	// 	$scope.typeName = typeName;
	// 	$scope.requestSchema = '?service=WFS&version=1.1.0&request=DescribeFeatureType&typeName=' + typeName;
	// 	$scope.requestItems = '?service=WFS&version=1.1.0&request=GetFeature&outputFormat=JSON&typeName=' + $scope.typeName;
		
	// 	$http.get($scope.endPoint + $scope.requestSchema)
	// 		.success(
	// 			function(data){
					
	// 				var format = new OpenLayers.Format.WFSDescribeFeatureType();
					
	// 				$scope.schema = (format.read(data).featureTypes[0].properties).map(function(it){
	// 					return createHeader(it.name);
	// 				});
	// 				console.log('schema', $scope.schema);
					

	// 				var columnDefs = $scope.schema;
	// 				console.log('columnDefs', columnDefs);

	// 				var rowData = [
	// 				    {make: "Toyota", model: "Celica", price: 35000},
	// 				    {make: "Ford", model: "Mondeo", price: 32000},
	// 				    {make: "Porsche", model: "Boxter", price: 72000}
	// 				];

	// 				$scope.gridOptions = {
	// 				    columnDefs: columnDefs,
	// 				    rowData: rowData
	// 				};

	// 		});

	// }



}]);

// omarApp.controller('ImagesTableCtrl', ['$scope', '$http',
//     function($scope, $http) 
//     {
//         console.log('controller...');
//         $scope.gridOptions = {
//             ready: function(api) { 
//             	console.log('ready');
//             	//get_data(api);
//         	}
//         };
//         // $scope.gridOptions.ready = function(api){
//         // 	alert('ready!');
//         // }
//         console.log($scope.gridOptions);

//         function get_data(api)
//         {
//         $http.get('images.json')
//             .then(
//                 function(res) 
//                 {
//                     $scope.gridOptions.columnDefs = res.data.columnDefs;
//                     $scope.gridOptions.rowData    = res.data.rowData;
//                     $scope.gridOptions.api.onNewCols();
//                     $scope.gridOptions.api.onNewRows();
//                 }
//             );
//         };
//     }
// ]);
