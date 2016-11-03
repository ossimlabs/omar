<div ng-controller="WfsController" class="container">
	<div class="row">
		<h1 class="alert alert-success">WFS Service Info</h1>
		<p>Use the following input to query a WFS end point.  &nbsp;A successful query will return a list of Feature
			Types.  &nbsp;Toggle through the feature types list to view sample data from the Feature Type.  Note: the
			sample data is limited to 50 items at initial load.</p> <p>Example WFS URL endpoint: <code>http://demo.boundlessgeo.com/geoserver/wfs</code></p>
		<form>
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label for="endPoint">WFS URL</label>
						<input id="endPoint" type="text" class="form-control" ng-model="endPoint"
							   placeholder="Enter a valid WFS URL endpoint">
						<!--<select id="endPoint" class="form-control" ng-model="endPoint">-->
							<!--<option>http://demo.boundlessgeo.com/geoserver/wfs</option>-->
							<!--<option>http://omar.ossim.org/omar/wfs</option>-->
							<!--<option>http://localhost:8080/geoserver/wfs</option>-->
							<!--<option>http://demo.opengeo.org/geoserver/wfs</option>-->
							<!--<option>http://giswebservices.massgis.state.ma.us/geoserver/wfs</option>-->
							<!--<option>http://clc.developpement-durable.gouv.fr/geoserver/wfs</option>-->
							<!--<option>http://localhost:8080/omar/wfs</option>-->
							<!--<option>http://localhost:7272/wfs</option>-->
							<!--<option>http://10.0.10.183/geoserver/wfs</option>-->
							<!--<option>http://10.0.10.183:9999/wfs</option>-->
						<!--</select>-->
					</div>
				</div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="version">Version</label>
						<select id="version" class="form-control" ng-model="version">
							<option>1.1.0</option>
							<option>1.0.0</option>
							<option>2.0.0</option>
						</select>
					</div>
				</div>
				<div class="col-md-2">
					<div class="form-group">
						<label for="outputFormat">Output</label>
						<select id="outputFormat" class="form-control" ng-model="outputFormat">
							<option>JSON</option>
							<option>GML3</option>
							<option>GML2</option>
						</select>
					</div>
				</div>
				<div class="col-md-2">
					<br>
					<button type="button" class="btn btn-primary" ng-click="getCapabilities()">Get Info</button>
				</div>
			</div>
			<br>
			<div class="row" ng-show="showFeatureTypeSelect">
				<div class="col-md-6">
					<p>Use the select box to choose a feature type, and view the associated feature name and types.</p>
				</div>
			</div>
			<div class="row" ng-show="showFeatureTypeSelect">
				<div class="col-md-6">
					<label for="featureType">Feature Types</label>
<!-- 						<select id="featureType" ng-change="describeFeature()" ng-model="selectedCapability" class="form-control">
						<option ng-repeat="capability in capabilities | orderBy:'name'" value="{{capability.name}}">{{capability.name}}</option>
					</select> -->
					<select id="featureType" ng-change="describeFeature()" ng-options="capability.name group by capability.featureNS for capability in capabilities" ng-model="selectedCapability" class="form-control">
					</select>
				</div>
				<div class="col-md-6">
					<div class="form-group">
						<label for="featureNamespace">Namespace</label>
						<input id="featureNamespace" type="text" class="form-control" ng-model="selectedCapability.featureNS" disabled>
					</div>
				</div>
			</div>
			<div class="row" ng-show="showFeatureTypeTable">
				<div class="col-md-6">
					<label for="featureType">CQL Filter</label>
					<input id="filter" type="text" class="form-control" ng-model="filter"
						   placeholder="STATE_NAME='Indiana'">
				</div>
				<div class="col-md-4">
					<label for="maxFeatures">Max Features</label>
					<!--<input id="maxFeatures" type="text" class="form-control" ng-model="maxFeatures">-->
					<select id="maxFeatures" class="form-control" ng-model="maxFeatures">
						<option>5</option>
						<option>10</option>
						<option>25</option>
						<option>50</option>
						<option>100</option>
						<option>200</option>
						<option>500</option>
						<option>1000</option>
					</select>
				</div>
				<div class="col-md-2">
					<br>
					<button type="button" class="btn btn-primary" ng-click="getFeature()">Refresh</button>
				</div>
			</div>
		</form>
	</div>
	<br>
	<div class="row" ng-show="showFeatureTypeTable">
		<div class="col-md-4">
			<div class="alert alert-info" role="alert">Describe Feature List</div>
		</div>
		<div class="col-md-8">
			<div class="alert alert-info" role="alert">GetFeature(s)</div>
		</div>
	</div>
	<div class="row" ng-show="showFeatureTypeTable" >
		<div class="col-md-4">
			<div class="the-container">
				<table id="describFeatureList" class="table table-striped the-table">
					<thead>
						<tr>
							<th>Name</th>
							<th>Type</th>
						</tr>
					</thead>
					<tr ng-repeat="column in columns">
						<td>{{column.name}}</td><td>{{column.type}}</td>
					</tr>
				</table>
			</div>
		</div>
		<div class="col-md-8">
			<div class="the-container" ng-show="showGetFeatureTable">
				<span us-spinner spinner-key="spinner"></span>
				<table class="table table-striped the-table">
					<thead>
						<tr>
							<th ng-repeat="column in columns" >{{column.name}}</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="feature in features">
							<!-- Below uses wfsclient.js -->
							<!-- <td ng-repeat="column in columns">{{feature.attributes[column.name]}}</td> -->
							<!-- Below uses angular $http -->
							<td ng-repeat="column in columns">{{feature.properties[column.name]}}</td>
						</tr>
					</tbody>
				</table>
				<br>
			</div>
		</div>
	</div>
</div>

