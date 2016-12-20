var anAnnotationHasBeenAddedMap = anAnnotationHasBeenAdded;
anAnnotationHasBeenAdded = function(event) {
	anAnnotationHasBeenAddedMap(event);

	removeInteractions();

	event.feature.setStyle(createDefaultStyle());
	tlv.selectAnnotationInteraction = new ol.interaction.Select({ features: [event.feature] });
	tlv.map.addInteraction(tlv.selectAnnotationInteraction);
	openAnnotationsDialog(event.feature);
}

function applyAnnotationStyle() {
	var feature = tlv.selectAnnotationInteraction.getFeatures().getArray()[0];

	var fillColorHex = $("#fillColorInput").val();
	var fillColor = hexToRgb(fillColorHex);
	var fillTransparency = $("#fillTransparencyInput").val();
	var fillRgba = "rgba(" + fillColor.r + "," + fillColor.g + "," + fillColor.b + "," + fillTransparency + ")";
	var fill = new ol.style.Fill({ color: fillRgba });

	var radius = $("#radiusInput").val();

	var strokeColorHex = $("#strokeColorInput").val();
	var strokeColor = hexToRgb(strokeColorHex);
	var strokeTransparency = $("#strokeTransparencyInput").val();
	var strokeRgba = "rgba(" + strokeColor.r + "," + strokeColor.g + "," + strokeColor.b + "," + strokeTransparency + ")";
	var strokeWidth = parseInt($("#strokeWidthInput").val(), 10);
	var stroke = new ol.style.Stroke({
		color: strokeRgba,
		width: strokeWidth
	});

	var style;
	switch (feature.getGeometry().getType()) {
		case "Point":
			var image = new ol.style.Circle({
					fill: fill,
					radius: parseInt(radius, 10),
					stroke: stroke
				});
			style = new ol.style.Style({ image: image });
			break;
		case "Circle":
			var center = ol.proj.transform(feature.getGeometry().getCenter(), "EPSG:3857", "EPSG:4326");
			var geometry = calculateCircleFromRadius(center, radius);
			feature.setGeometry(geometry);
		default:
			style = new ol.style.Style({
				fill: fill,
				stroke: stroke
			});
			break;
	}
	feature.setStyle(style);

	// refresh the layer for the new style to take effect
	tlv.layers[tlv.currentLayer].annotationsLayer.setVisible(false);
	tlv.layers[tlv.currentLayer].annotationsLayer.setVisible(true);
}

function calculateCircleFromRadius(center, radius) {
	var sphere = new ol.Sphere(6378137);

	var point = [center[0], center[1]];
	var distance = 0;
	while (distance < radius) {
		point[0] += 0.000001;
		distance = sphere.haversineDistance(center, point);
	}

	var projectedPoint1 = ol.proj.transform(center, "EPSG:4326", "EPSG:3857");
	var projectedPoint2 = ol.proj.transform(point, "EPSG:4326", "EPSG:3857");
	var newRadius = Math.abs(projectedPoint2[0] - projectedPoint1[0]);
	var geometry = new ol.geom.Circle(projectedPoint1, newRadius);


	return geometry;
}

var changeFrameAnnotations = changeFrame;
changeFrame = function(param) {
	var annotationsLayer = tlv.layers[tlv.currentLayer].annotationsLayer;
	if (annotationsLayer) { annotationsLayer.setVisible(false); }

	changeFrameAnnotations(param);

	var annotationsLayer = tlv.layers[tlv.currentLayer].annotationsLayer;
	if (annotationsLayer) { annotationsLayer.setVisible(true); }

	removeInteractions();
}

function componentToHex(component) {
    var hex = component.toString(16);


    return hex.length == 1 ? "0" + hex : hex;
}

function createAnnotationsLayer() {
	var source = new ol.source.Vector();
	source.on("addfeature", anAnnotationHasBeenAdded);

	var layer = tlv.layers[tlv.currentLayer];
	layer.annotationsLayer = new ol.layer.Vector({ source: source });
	tlv.map.addLayer(layer.annotationsLayer);
}

function createDefaultStyle() {
	return new ol.style.Style({
		fill: new ol.style.Fill({ color: "rgba(255, 255, 0, 0)" }),
		image: new ol.style.Circle({
			fill: new ol.style.Fill({ color: "rgba(255, 255, 0, 1)" }),
			radius: 5,
			stroke: new ol.style.Stroke({
				color: "rgba(255, 255, 0, 0)",
	            width: 2
	 		})
		}),
		stroke: new ol.style.Stroke({
			color: "rgba(255, 255, 0, 1)",
            width: 2
 		})
	});
}

function deleteFeature() {
	var feature = tlv.selectAnnotationInteraction.getFeatures().getArray()[0];
	var source = tlv.layers[tlv.currentLayer].annotationsLayer.getSource();
	source.removeFeature(feature);
}

function drawCircle() {
	tlv.drawAnnotationInteraction = new ol.interaction.Draw({
        source: tlv.layers[tlv.currentLayer].annotationsLayer.getSource(),
		type: "Circle"
	});
}

function drawLineString() {
	tlv.drawAnnotationInteraction = new ol.interaction.Draw({
        source: tlv.layers[tlv.currentLayer].annotationsLayer.getSource(),
		type: "LineString"
	});
}

function drawPoint() {
	tlv.drawAnnotationInteraction = new ol.interaction.Draw({
        source: tlv.layers[tlv.currentLayer].annotationsLayer.getSource(),
		type: "Point"
	});
}

function drawPolygon() {
	tlv.drawAnnotationInteraction = new ol.interaction.Draw({
        source: tlv.layers[tlv.currentLayer].annotationsLayer.getSource(),
		type: "Polygon"
	});
}

function drawRectangle() {
	tlv.drawAnnotationInteraction = new ol.interaction.Draw({
		geometryFunction: function(coordinates, geometry) {
			if (!geometry) { geometry = new ol.geom.Polygon(null); }
			var start = coordinates[0];
			var end = coordinates[1];
			geometry.setCoordinates([[
				start,
				[start[0], end[1]],
				end,
				[end[0], start[1]],
				start
			]]);


			return geometry;
        },
		maxPoints: 2,
        source: tlv.layers[tlv.currentLayer].annotationsLayer.getSource(),
		type: "LineString"
	});
}

function drawSquare() {
	tlv.drawAnnotationInteraction = new ol.interaction.Draw({
		geometryFunction: ol.interaction.Draw.createRegularPolygon(4),
        source: tlv.layers[tlv.currentLayer].annotationsLayer.getSource(),
		type: "Circle"
	});
}

function drawAnnotationMap(type) {
	// create an annotations layer if one does not exist
	if (!tlv.layers[tlv.currentLayer].annotationsLayer) { createAnnotationsLayer(); }

	// create the right draw interaction
	switch (type) {
		case "circle": drawCircle(); break;
		case "lineString": drawLineString(); break;
		case "point": drawPoint(); break;
		case "polygon": drawPolygon(); break;
		case "rectangle": drawRectangle(); break;
		case "square": drawSquare(); break;
	}
	tlv.map.addInteraction(tlv.drawAnnotationInteraction);
}

function getCircleRadius(geometry) {
	var sphere = new ol.Sphere(6378137);
	var point1 = ol.proj.transform(geometry.getCenter(), "EPSG:3857", "EPSG:4326");
	var point2 = ol.proj.transform(geometry.getLastCoordinate(), "EPSG:3857", "EPSG:4326");
	var radius = sphere.haversineDistance(point1, point2);


	return radius;
}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);


	return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}

function modifyAnnotationsMap() {
	var layer = tlv.layers[tlv.currentLayer].annotationsLayer;
	if (layer) {
		var features = new ol.Collection(layer.getSource().getFeatures());
		if (features) {
			// allow vertices to be added and deleted
			tlv.modifyAnnotationsInteraction = new ol.interaction.Modify({
        		deleteCondition: function(event) {
          			return ol.events.condition.shiftKeyOnly(event) && ol.events.condition.singleClick(event);
        		},
				features: features
			});
    		tlv.map.addInteraction(tlv.modifyAnnotationsInteraction);

			// actually handle selecting the feature
			tlv.selectAnnotationInteraction = new ol.interaction.Select({ layers: [layer] });
			tlv.selectAnnotationInteraction.once(
				"select",
				function(event) {
					openAnnotationsDialog();
					removeInteractions();
				}
			);
			tlv.map.addInteraction(tlv.selectAnnotationInteraction);
		}
		else { displayErrorDialog("There are no annotations here to modify. :()"); }
	}
}

function openAnnotationsDialog() {
	$("#annotationsDialog").modal("show");

	var feature = tlv.selectAnnotationInteraction.getFeatures().getArray()[0];
	var style = feature.getStyle();

	var fillColor = ol.color.asArray(style.getFill().getColor());
	var radius;
	var radiusInput = $("#radiusInput");
	radiusInput.prop("disabled", false);
	radiusInput.prev().html("Radius");

	var stroke = style.getStroke();
	var strokeColor = ol.color.asArray(stroke.getColor());
  	var strokeWidth = stroke.getWidth();

	switch (feature.getGeometry().getType()) {
		case "Circle":
			radiusInput.prev().html("Radius (meters)");
			radius = getCircleRadius(feature.getGeometry());
			break;
		case "Point":
			var image = style.getImage();
			fillColor = ol.color.asArray(image.getFill().getColor());
			radius = image.getRadius();
			radiusInput.prev().html("Radius (pixels)");
			var stroke = style.getImage().getStroke();
			strokeColor = ol.color.asArray(stroke.getColor());
			strokeWidth = stroke.getWidth();
			break;
		default:
			radiusInput.prop("disabled", true);
			break;
	}

	var fillColorHex = rgbToHex(fillColor[0], fillColor[1], fillColor[2]);
	$("#fillColorInput").val(fillColorHex);
	$("#fillTransparencyInput").val(fillColor[3]);

	radiusInput.val(radius || 0);

	var strokeColorHex = rgbToHex(strokeColor[0], strokeColor[1], strokeColor[2]);
	$("#strokeColorInput").val(strokeColorHex);
	$("#strokeTransparencyInput").val(strokeColor[3]);
	$("#strokeWidthInput").val(strokeWidth);
}

function removeInteractions() {
	$.each(
		[tlv.drawAnnotationInteraction, tlv.modifyAnnotationsInteraction, tlv.selectAnnotationInteraction],
		function(i, x) {
			// make sure there is an interaction to remove first
			if (x) {
				tlv.map.removeInteraction(x);
				x = null;
			}
		}
	);
}

function rgbToHex(r, g, b) { return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b); }
