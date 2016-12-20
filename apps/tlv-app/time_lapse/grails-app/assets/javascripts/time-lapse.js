function aFeatureHasBeenSelected(feature, event) {}

function buildSummaryTable() {
	var table = $("#timeLapseSummaryTable")[0];

	for (var i = table.rows.length - 1; i >= 0; i--) { table.deleteRow(i); }

	var row = table.insertRow(0);
	var cell = row.insertCell(row.cells.length);
	row.insertCell(row.cells.length);
	var keys = ["imageId", "acquisitionDate", "library"];
	$.each(
		keys,
		function(i, x) {
			var cell = row.insertCell(row.cells.length);
			$(cell).append(x.capitalize().replace(/([A-Z])/g, " $1"));
		}
	);

	$.each(
		tlv.layers,
		function(i, x) {
			row = table.insertRow(table.rows.length);
			cell = row.insertCell(row.cells.length);
			$(cell).append(i + 1);

			cell = row.insertCell(row.cells.length);
			$(cell).css("white-space", "nowrap");
			var downButton = "<span class = 'glyphicon glyphicon-arrow-down' title = 'Move Down'></span>";
			var upButton = "<span class = 'glyphicon glyphicon-arrow-up' title = 'Move Up'></span>";

			if (i == 0) {
				$(cell).append("<a href = javascript:moveLayerDownInStack(" + i + ");buildSummaryTable();>" + downButton + "</a>");
			}
			else if (i == tlv.layers.length - 1) { $(cell).append("<a href = javascript:moveLayerUpInStack(" + i + ");buildSummaryTable();>" + upButton + "</a>"); }
			else {
				$(cell).append("<a href = javascript:moveLayerUpInStack(" + i + ");buildSummaryTable();>" + upButton + "</a>");
				$(cell).append("<a href = javascript:moveLayerDownInStack(" + i + ");buildSummaryTable();>" + downButton + "</a>");
			}

			cell = row.insertCell(row.cells.length);
			$(cell).append(x.imageId);

			cell = row.insertCell(row.cells.length);
			$(cell).append(x.acquisitionDate);

			cell = row.insertCell(row.cells.length);
			$(cell).append(tlv.availableResources.complete[x.library].label);
		}
	);
}

function calculateInitialViewBbox() {
        var bbox;
        if (typeof tlv.bbox == "string") {
                var array = tlv.bbox.split(",").map(Number);
                bbox = { minLon: array[0], minLat: array[1], maxLon: array[2], maxLat: array[3] };
        }
        else { bbox = convertRadiusToBbox(tlv.location[0], tlv.location[1], 1000); }


        return [bbox.minLon, bbox.minLat, bbox.maxLon, bbox.maxLat];
}

function changeFrame(param) {
	var layer = tlv.layers[tlv.currentLayer];
	layer.mapLayer.setOpacity(0);
	layer.mapLayer.setVisible(layer.keepVisible);

	if (param === "fastForward") { tlv.currentLayer = getNextFrameIndex(); }
	else if (param === "rewind") { tlv.currentLayer = getPreviousFrameIndex(); }
	else if (typeof param === "number") { tlv.currentLayer = param; }

	tlv.layers[tlv.currentLayer].mapLayer.setVisible(true);
	tlv.layers[tlv.currentLayer].mapLayer.setOpacity(1);

	tlv.map.renderSync();

	updateScreenText();
	updateTileLoadingProgressBar();
}

function deleteFrame() {
	changeFrame("rewind");

	var nextFrameIndex = getNextFrameIndex();
	var spliceIndex = nextFrameIndex == 0 ? 0 : nextFrameIndex;
	tlv.layers.splice(spliceIndex, 1);
	if (tlv.currentLayer > tlv.layers.length - 1) { tlv.currentLayer = tlv.layers.length - 1; }

	changeFrame("fastForward");
}

function geoJump(location) {
	var point = convertGeospatialCoordinateFormat(
		location,
		function(point) {
			if (point) { tlv.map.getView().setCenter(ol.proj.transform(point, "EPSG:4326", "EPSG:3857")); }
		}
	);
}

function getCurrentDimension() {
	var dimension = $("#dimensionsSelect").val();


	return parseInt(dimension, 10);
}

function getNextFrameIndex() { return tlv.currentLayer >= tlv.layers.length - 1 ? 0 : tlv.currentLayer + 1; }

function getPreviousFrameIndex() { return tlv.currentLayer <= 0 ? tlv.layers.length - 1 : tlv.currentLayer - 1; }

function getTimeToAdjacentImage(layerIndex, adjacency) {
	var layerIndex2 = null;
	if (adjacency == "previous" && layerIndex > 0) { layerIndex2 = layerIndex - 1; }
	else if (adjacency == "next" && layerIndex < tlv.layers.length - 1) { layerIndex2 = layerIndex + 1; }

	if (typeof layerIndex2 == "number") {
		var date1 = tlv.layers[layerIndex].acquisitionDate ? new Date(Date.parse(tlv.layers[layerIndex].acquisitionDate)) : null;
		var date2 = tlv.layers[layerIndex2].acquisitionDate ? new Date(Date.parse(tlv.layers[layerIndex2].acquisitionDate)) : null;

		if (date1 && date2) {
			var timeDifference = Math.abs(date2 - date1);
			var seconds = parseInt(timeDifference / 1000);

			var minutes = parseInt(seconds / 60);
			if (minutes > 0) { seconds -= minutes * 60; }

			var hours = parseInt(minutes / 60);
			if (hours > 0) { minutes -= hours * 60; }

			var days = parseInt(hours / 24);
			if (days > 0) { hours -= days * 24; }

			var months = parseInt(days / 30);
			if (months > 0) { days -= months * 30; }

			var years = parseInt(months / 12);
			if (years > 0) { months -= years * 12; }


			if (years > 0) {
				if (months > 0) { return "~" + years + "yr., " + months + " mon."; }
				else { return "~" + years + "yr."; }
			}
			else if (months > 0) {
				if (days > 0) { return "~" + months + "mon., " + days + "dy."; }
				else { return "~" + months + "mon."; }
			}
			else if (days > 0) {
				if (hours > 0) { return "~" + days + "dy., " + hours + "hr."; }
				else { return "~" + days + "dy."; }
			}
			else if (hours > 0) {
				if (minutes > 0) { return "~" + hours + "hr., " + minutes + "min."; }
				else { return "~" + hours + "hr."; }
			}
			else if (minutes > 0) {
				if (seconds > 0) { return "~" + minutes + "min., " + seconds + "sec."; }
				else { return "~" + minutes + "min."; }
			}
			else if (seconds > 0) { return "~" + seconds + "sec."; }
			else { return "0 sec."; }
		}
	}
	else { return false; }
}

function moveLayerDownInStack(layerIndex) {
	var nextLayerIndex = layerIndex + 1;
	if (nextLayerIndex < tlv.layers.length)	{
		var thisLayer = tlv.layers[layerIndex];
		tlv.layers[layerIndex] = tlv.layers[nextLayerIndex];
		tlv.layers[nextLayerIndex] = thisLayer;

		var collection = tlv.map.getLayers();
		var element = collection.removeAt(tlv.layers.length - 1 - layerIndex);
        	collection.insertAt(tlv.layers.length - 1 - nextLayerIndex, element);
	}

	changeFrame("fastForward");
	changeFrame("rewind");

	if ($("#summaryTableDialog").hasClass("in")) { buildSummaryTable(); }
}

function moveLayerUpInStack(layerIndex) {
	var collection = tlv.map.getLayers();
	var element = collection.getArray()[tlv.layers.length - 1 - layerIndex];

	var previousLayerIndex = layerIndex - 1;
	if (previousLayerIndex >= 0) {
		var thisLayer = tlv.layers[layerIndex];
		tlv.layers[layerIndex] = tlv.layers[previousLayerIndex];
		tlv.layers[previousLayerIndex] = thisLayer;

		var collection = tlv.map.getLayers();
		var element = collection.removeAt(tlv.layers.length - 1 - layerIndex);
		collection.insertAt(tlv.layers.length - 1 - previousLayerIndex, element);
	}

	changeFrame("fastForward");
	changeFrame("rewind");

	if ($("#summaryTableDialog").hasClass("in")) { buildSummaryTable(); }
}

function orientDevice(event) {
	if (getCurrentDimension() == 2) {
		if (event.alpha) { tlv.map.getView().rotate((275 + event.alpha) * Math.PI / 180); }
	}
	else {
		if (event.alpha && event.beta && event.gamma) {
			tlv.globe.getCesiumScene().camera.setView({
				orientation: {
					heading: (90 - event.alpha) * Math.PI / 180,
					pitch: (event.beta - 90) * Math.PI / 180
				}
			});
		}
	}
}

function orientationToggle() {
	if ($("#orientationSelect").val() == "auto") {
		if (window.DeviceOrientationEvent) { window.addEventListener("deviceorientation", orientDevice, false); }
		else {
			$("#orientationSelect").val("manual");
			displayErrorDialog("Sorry, your device doesn't support device orientation. :(");
		}
	}
	else { window.removeEventListener("deviceorientation", orientDevice, false); }
}

var pageLoadTimeLapse = pageLoad;
pageLoad = function() {
	pageLoadTimeLapse();

	tlv.tooltipInfo = $("#tooltipInfo");
	tlv.tooltipInfo.tooltip({
		animation: false,
		trigger: "manual"
	});

	if (tlv.layers) {
		$("#searchDialog").modal("hide");
		tlv.bbox = calculateInitialViewBbox();
		setupTimeLapse();
	}
}

function playStopTimeLapse(button) {
	var className = button.className;

	$(button).removeClass(className);
	if (className.contains("play")) {
		playTimeLapse();
		className = className.replace("play", "stop");
	}
	else {
		stopTimeLapse();
		className = className.replace("stop", "play");
	}
	$(button).addClass(className);
}

function playTimeLapse() {
	changeFrame("fastForward");
	tlv.timeLapseAdvance = setTimeout("playTimeLapse()", 1000);
}

function reverseOrder() {
	tlv.layers.reverse();
	changeFrame('rewind');
	changeFrame('fastForward');
}

function setupTimeLapse() {
	setupMap();
	addBaseLayersToTheMap();

	if (tlv.chronological == "false") { tlv.layers.reverse(); }
	// add layers to the map
	$.each(
		tlv.layers,
		function(i, x) {
			x.keepVisible = x.keepVisible || false;
			addLayerToTheMap(x);
		}
	);
	tlv.currentLayer = 0;

	var extent = ol.proj.transformExtent(tlv.bbox, "EPSG:4326", "EPSG:3857");
	tlv.map.getView().fit(extent, tlv.map.getSize());

	// register map listeners
	tlv.map.on("moveend", theMapHasMoved);
	tlv.map.on("pointermove", function(event) {
		var feature = tlv.map.forEachFeatureAtPixel(event.pixel, function(feature, layer) { return feature; });
		if (feature) { aFeatureHasBeenSelected(feature, event); }
		else { tlv.tooltipInfo.tooltip("hide"); }
	});


	tlv.layers[0].mapLayer.setVisible(true);
	tlv.layers[0].mapLayer.setOpacity(1);

	enableMenuButtons();

	updateScreenText();
}

function stopTimeLapse() { clearTimeout(tlv.timeLapseAdvance); }

function updateAcquisitionDate() {
	var acquisitionDate = tlv.layers[tlv.currentLayer].acquisitionDate;
	if (acquisitionDate) {
		var timeToNextImage = getTimeToAdjacentImage(tlv.currentLayer, "next");
		var timeToPreviousImage = getTimeToAdjacentImage(tlv.currentLayer, "previous");
		$("#acquisitionDateDiv").html(
			(timeToPreviousImage ? timeToPreviousImage + " <- " : "") +
			acquisitionDate + "z" +
			(timeToNextImage ? " -> " + timeToNextImage : "")
		);
	}
	else { $("#acquisitionDateDiv").html("N/A"); }
}

function updateImageId() {
	var layer = tlv.layers[tlv.currentLayer];
	var libraryLabel = tlv.availableResources.complete[layer.library].label;
	$("#imageIdDiv").html(libraryLabel + ": " + layer.imageId);
}

function updateScreenText() {
	updateImageId();
	updateAcquisitionDate();
	updateTlvLayerCount();
}

function updateTlvLayerCount() {
	var currentCount = tlv.currentLayer + 1;
	$("#tlvLayerCountSpan").html(currentCount + "/" + tlv.layers.length);
}
