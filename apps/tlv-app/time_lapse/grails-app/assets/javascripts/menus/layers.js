function crossHairLayerToggle() {
	var state = $("#layersCrossHairSelect").val();
	if (state == "on") { displayCrossHairLayer(); }
	else { hideCrossHairLayer(); }
}

function displayCrossHairLayer() {
	if (!tlv.crossHairLayer) {
		var stroke = new ol.style.Stroke({
			 color: "rgba(255, 255, 0, 1)",
			 width: 2
		});
		var style = new ol.style.Style({ stroke: stroke });

		tlv.crossHairLayer = new ol.layer.Vector({
			source: new ol.source.Vector(),
			style: style
		});
		tlv.map.addLayer(tlv.crossHairLayer);
	}
	else { tlv.crossHairLayer.setVisible(true); }
	refreshCrossHairLayer();
}

function displaySearchOriginLayer() {
	if (!tlv.searchOriginLayer) {
		var point = new ol.geom.Point(tlv.location).transform("EPSG:4326", "EPSG:3857");
		var feature = new ol.Feature(point);

		var fill = new ol.style.Fill({ color: "rgba(255, 255, 0, 1)"});
		var circle = new ol.style.Circle({
			fill: fill,
			radius: 5
		});
		var text = new ol.style.Text({
			fill: fill,
			font: "10px sans-serif",
			offsetY: 13,
			text: "Search Origin"
		});
		var style = new ol.style.Style({
			image: circle,
			text: text
		});

		tlv.searchOriginLayer = new ol.layer.Vector({
			source: new ol.source.Vector({ features: [feature] }),
			style: style
		});
		tlv.map.addLayer(tlv.searchOriginLayer);
	}
	else { tlv.searchOriginLayer.setVisible(true); }
}

function hideCrossHairLayer() { tlv.crossHairLayer.setVisible(false); }

function hideSearchOriginLayer() { tlv.searchOriginLayer.setVisible(false); }

function refreshCrossHairLayer() {
	var mapCenter = tlv.map.getView().getCenter();

	var centerPixel = tlv.map.getPixelFromCoordinate(mapCenter);
	var deltaXPixel = [centerPixel[0] + 10, centerPixel[1]];
	var deltaYPixel = [centerPixel[0], centerPixel[1] + 10];

	var deltaXDegrees = tlv.map.getCoordinateFromPixel(deltaXPixel)[0] - mapCenter[0];
	var deltaYDegrees = tlv.map.getCoordinateFromPixel(deltaYPixel)[1] - mapCenter[1];

	var horizontalLinePoints = [
		[mapCenter[0] - deltaXDegrees, mapCenter[1]],
		[mapCenter[0] + deltaXDegrees, mapCenter[1]]
	];
	var horizontalLineGeometry = new ol.geom.LineString(horizontalLinePoints);
	var horizontalLineFeature = new ol.Feature(horizontalLineGeometry);

	var verticalLinePoints = [
		[mapCenter[0], mapCenter[1] - deltaYDegrees],
		[mapCenter[0], mapCenter[1] + deltaYDegrees]
	];
	var verticalLineGeometry = new ol.geom.LineString(verticalLinePoints);
	var verticalLineFeature = new ol.Feature(verticalLineGeometry);

	var source = tlv.crossHairLayer.getSource();
        $.each(source.getFeatures(), function(i, x) { source.removeFeature(x); });
	source.addFeatures([horizontalLineFeature, verticalLineFeature]);
}

function searchOriginLayerToggle() {
	var state = $("#layersSearchOriginSelect").val();
	if (state == "on") { displaySearchOriginLayer(); }
	else { hideSearchOriginLayer(); }
}

var setupTimeLapseLayers = setupTimeLapse;
setupTimeLapse = function() {
	setupTimeLapseLayers();

	tlv.crossHairLayer = null;
	var crossHairLayerSelect = $("#layersCrossHairSelect");
	if (tlv.crossHairLayerEnabled == "true" || crossHairLayerSelect.val() == "on") {
		crossHairLayerSelect.val("on");
		crossHairLayerToggle();
	}

	tlv.searchOriginLayer = null;
	var searchOriginLayerSelect = $("#layersSearchOriginSelect");
	if (tlv.searchOriginLayerEnabled == "true" || searchOriginLayerSelect.val() == "on") {
		searchOriginLayerSelect.val("on");
		searchOriginLayerToggle();
	}
}

var theMapHasMovedLayers = theMapHasMoved;
theMapHasMoved = function() {
	theMapHasMovedLayers();

	if ($("#layersCrossHairSelect").val() == "on") { refreshCrossHairLayer(); }
}
