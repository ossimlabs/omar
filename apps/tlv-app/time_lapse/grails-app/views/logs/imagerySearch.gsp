<!DOCTYPE html>
<html>
	<head>
		<meta content = "logs" name = "layout"/>
		<g:set var = "entityName" value = "${message(code: 'imagerySearch.label', default: 'ImagerySearch')}"/>
		<title><g:message code="default.list.label" args="[entityName]"/></title>
	</head>
	<body>
		<div id ="list-imagerySearch" class = "content scaffold-list" role = "main">
			<h1>
				<g:message code="default.list.label" args="[entityName]"/>
				<button class = "btn btn-primary" onclick = exportLogsTable() title = "Export Table">
					<span class = "glyphicon glyphicon-download-alt"></span>
				</button>
			</h1>

			<div class = "map" id = "map"></div>

			<f:table collection="${imagerySearchList}" />

			<div class="pagination">
				<g:paginate total="${imagerySearchCount ?: 0}" />
			</div>
		</div>

		<g:javascript>
			var dateLayer;
			var map;
			loadStylesheet();

			function addDateToMap() {
				var table = $("body").find("table")[0];
				var dateCell = 0;
				$.each(
					table.rows[0].cells,
					function(i, x) {
						var cell = $(x).children()[0].innerHTML;
						if (cell.contains("Date") && !cell.contains("Start") && !cell.contains("End")) { dateCell = i; }
					}
				);

				var startDate = table.rows[1].cells[dateCell].innerHTML;
				startDate = Date.parse(startDate);
				var endDate = table.rows[table.rows.length - 1].cells[dateCell].innerHTML;
				endDate = Date.parse(endDate);
				if (startDate > endDate) {
					var dateTemp = startDate;
					startDate = endDate;
					endDate = dateTemp;
				}

				var dateFeature = new ol.Feature({
					geometry: new ol.geom.Point(map.getView().getCenter())
				});
				dateFeature.setStyle(new ol.style.Style({
					text: new ol.style.Text({
						fill: new ol.style.Fill({ color: "rgba(0, 0, 0, 1)"}),
						font: "10px sans-serif",
						offsetY: -10,
						text: formatDate(new Date(startDate)) + " to " + formatDate(new Date(endDate))
					})
				}));

				dateLayer = new ol.layer.Vector({
					opacity: 0,
					source: new ol.source.Vector({
						features: [dateFeature]
					})
				});
				map.addLayer(dateLayer);
			}

			function formatDate(date) {
				var day = date.getDate();
				if (day < 10) { day = "0" + day; }
				var hours = date.getHours();
				if (hours < 10) { hours = "0" + hours; }
				var minutes = date.getMinutes();
				if (minutes < 10) { minutes = "0" + minutes; }
 				var month = date.getMonth() + 1;
				if (month < 10) { month = "0" + month; }
				var seconds = date.getSeconds();
				if (seconds < 10) { seconds = "0" + seconds; }
				var year = date.getFullYear();

				return year + "-" + month + "-" + day  + " " + hours + ":" + minutes + ":" + seconds;
			}

			function loadStylesheet() {
				var link = document.createElement("link");
				link.href = "/assets/ol-3.15.1.css";
				link.onload = function() { loadJavascript(); }
				link.rel = "stylesheet";
				link.type = "text/css";
				document.getElementsByTagName("head")[0].appendChild(link);
			}

			function loadJavascript() {
				var script = document.createElement('script');
				script.async = true;
				script.onload = function() {
					setupMap();
					addDateToMap();
				}
				script.src = "/assets/ol3-cesium-debug-1.16.js";
				script.type = "text/javascript";

				document.getElementsByTagName('head')[0].appendChild(script);
			}

			var exportLogsTableImagerySearch = exportLogsTable;
			exportLogsTable = function() {
				exportLogsTableImagerySearch();

				refreshDateLayer();
				dateLayer.setOpacity(1);
				map.once(
					"postcompose",
					function(event) {
						var canvas = event.context.canvas;
						canvas.toBlob(function(blob) {
							var filename = "tlv_logs_heatmap_" + new Date().generateFilename() + ".png";
							clientFileDownload(filename, blob);
							dateLayer.setOpacity(0);
						});
					}
				);
				map.renderSync();
			}

			function refreshDateLayer() {
				var extent = map.getView().calculateExtent(map.getSize());
				var lowerCenter = [ol.extent.getCenter(extent)[0], extent[1]];
				dateLayer.getSource().getFeatures()[0].setGeometry(new ol.geom.Point(lowerCenter));
			}

			function setupMap() {
				map = new ol.Map({
					layers: [
						new ol.layer.Vector({
							source: new ol.source.Vector({
								url: "/world/world.geojson",
								format: new ol.format.GeoJSON()
							})
						})
					],
					logo: false,
					view: new ol.View({
						center: [0, 0],
						projection: "EPSG:4326"
					}),
					target: "map"
		        });
				map.getView().fit([-180, -90, 180, 90], map.getSize());

				// find the location cell in table
				var table = $("body").find("table")[0];
				var locationCell = 0;
				$.each(
					table.rows[0].cells,
					function(i, x) {
						var cell = $(x).children()[0].innerHTML;
						if (cell.contains("Location")) { locationCell = i; }
					}
				);

				var features = [];
				$.each(
					table.rows,
					function(i, x) {
						if (i != 0) {
							var location = x.cells[locationCell].innerHTML.split(",");
							var longitude = parseFloat(location[0]);
							var latitude = parseFloat(location[1]);
							var point = new ol.geom.Point([longitude, latitude]);
							var feature = new ol.Feature({ geometry: point });
							features.push(feature);
						}
					}
				);
				var source = new ol.source.Vector({ features: features });

				// add heatmap
				var heatmap = new ol.layer.Heatmap({
					radius: 10,
					source: source
				});
				map.addLayer(heatmap);

				// add cluster layer
				var clusterLayer = new ol.layer.Vector({
					source: new ol.source.Cluster({
						distance: 10,
						source: source
					}),
					style: function(feature) {
						return new ol.style.Style({
							image: new ol.style.Circle({
								fill: new ol.style.Fill({ color: "rgba(0, 0, 255, 0.5)" }),
								radius: 10,
								stroke: new ol.style.Stroke({ color: "rgba(0, 0, 0, 0)" })
							}),
							text: new ol.style.Text({
								text: feature.get("features").length.toString(),
								fill: new ol.style.Fill({ color: "rgba(255, 255, 255, 1)" })
							})
						});
					}
		        });
				map.addLayer(clusterLayer);
			}
		</g:javascript>
	</body>
</html>
