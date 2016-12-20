function addDimension() {
	if (checkWebGlCompatability()) { tlv.globe.setEnabled(true); }
	else { $("#dimensionsSelect").val(2); }
}

function addSwipeListenerToMap() {
	var firstLayer, secondLayer = null;
	if (!firstLayer) { firstLayer = tlv.currentLayer; }
	if (!secondLayer) { secondLayer = tlv.currentLayer >= tlv.layers.length - 1 ? 0 : tlv.currentLayer + 1; }

	tlv.layers[firstLayer].mapLayer.setVisible(true);
	tlv.layers[firstLayer].mapLayer.setOpacity(1);
	tlv.layers[secondLayer].mapLayer.setVisible(true);
	tlv.layers[secondLayer].mapLayer.setOpacity(1);

	tlv.swipeLayers = [firstLayer, secondLayer].sort();
	tlv.layers[tlv.swipeLayers[1]].mapLayer.on("precompose", precomposeSwipe);
	tlv.layers[tlv.swipeLayers[1]].mapLayer.on("postcompose", postcomposeSwipe);
}

var changeFrameView = changeFrame;
	changeFrame = function(params) {
	if ($("#swipeSelect").val() == "on") {
		turnOffSwipe();
		changeFrameView(params);
		turnOnSwipe();
	}
	else { changeFrameView(params); }
}

var createMapControlsView = createMapControls;
createMapControls = function() {
	createMapControlsView();

	$.each(createSwipeControls(), function(i, x) { tlv.mapControls.push(x); });
}

function createSwipeControls() {
	var leftSwipeTextDiv = document.createElement("div");
	leftSwipeTextDiv.className = "swipe-text-div swipe-text-div-left";
	leftSwipeTextDiv.id = "leftSwipeTextDiv";
	var leftSwipeTextControl = new ol.control.Control({ element: leftSwipeTextDiv });

	var rightSwipeTextDiv = document.createElement("div");
	rightSwipeTextDiv.className = "swipe-text-div swipe-text-div-right";
	rightSwipeTextDiv.id = "rightSwipeTextDiv";
	var rightSwipeTextControl = new ol.control.Control({ element: rightSwipeTextDiv });

	var sliderInput = document.createElement("input");
	sliderInput.id = "swipeSliderInput";
	sliderInput.style = "width: 100%";
	sliderInput.type = "text";
	sliderInput.setAttribute("data-slider-id", "swipeSlider");
	var sliderControl = new ol.control.Control({ element: sliderInput });


	return [leftSwipeTextControl, rightSwipeTextControl, sliderControl];
}

function dimensionToggle() {
	var state = $("#dimensionsSelect").val();
	if (state == 2) { removeDimension(); }
	else { addDimension(); }
}

function initializeSwipeSlider() {
	var swipeSlider = $("#swipeSliderInput");
	swipeSlider.slider({
		max: 100,
		min: 0,
		tooltip: "hide",
		value: 50
	});
	swipeSlider.on("slide", function() { tlv.map.render(); });

	$("#swipeSlider").hide();
}

var precomposeSwipe = function(event) {
	var context = event.context;
	var width = context.canvas.width * $("#swipeSliderInput").slider("getValue") / 100;

	context.save();
	context.beginPath();
	context.rect(width, 0, context.canvas.width - width, context.canvas.height);
	context.clip();
}

var postcomposeSwipe = function(event) { event.context.restore(); }

function removeDimension() { tlv.globe.setEnabled(false); }

function removeSwipeListenerFromMap() {
	$.each(
		tlv.layers,
		function(i, x) {
			x.mapLayer.un("precompose", precomposeSwipe);
			x.mapLayer.un("postcompose", postcomposeSwipe);
			x.mapLayer.setOpacity(0);
			x.mapLayer.setVisible(false);
		}
	);

	tlv.layers[tlv.currentLayer].mapLayer.setVisible(true);
	tlv.layers[tlv.currentLayer].mapLayer.setOpacity(1);
}

function swipeToggle() {
	var state = $("#swipeSelect").val();
	if (state == "on") { turnOnSwipe(); }
	else { turnOffSwipe(); }
}

var setupMapView = setupMap;
setupMap = function() {
	setupMapView();
	initializeSwipeSlider();
}

function turnOffSwipe() {
	$(".ol-full-screen").show();
	$(".ol-mouse-position").show();
	$(".ol-rotate").show();
	$(".ol-zoom").show();

	$("#leftSwipeTextDiv").hide();
	$("#rightSwipeTextDiv").hide();
	$("#swipeSlider").hide();
	removeSwipeListenerFromMap();

	updateScreenText();
}

function turnOnSwipe() {
	$(".ol-full-screen").hide();
	$(".ol-mouse-position").hide();
	$(".ol-rotate").hide();
	$(".ol-zoom").hide();

	$("#leftSwipeTextDiv").show();
	$("#rightSwipeTextDiv").show();
	$("#swipeSlider").show();
	addSwipeListenerToMap();

	updateScreenText();
}

var updateScreenTextView = updateScreenText;
updateScreenText = function() {
	updateScreenTextView();

	if ($("#swipeSelect").val() == "on") {
		$("#acquisitionDateDiv").html("&nbsp;");
		$("#imageIdDiv").html("&nbsp;");

		$.each(
			{ left: tlv.swipeLayers[0], right: tlv.swipeLayers[1] },
			function(i, x) {
				var layer = tlv.layers[x];
				var acquisitionDate = layer.acquisitionDate;
				var imageId = layer.imageId;
				var libraryLabel = tlv.availableResources.complete[layer.library].label;
				$("#" + i + "SwipeTextDiv").html(libraryLabel + ": " + imageId + "<br>" + acquisitionDate);
			}
		);
	}
}
