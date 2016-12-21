function convertGeospatialCoordinateFormat(inputString, callbackFunction) {
	var ddPattern = /(\-?\d{1,2}[.]?\d*)[\s+|,?]\s*(\-?\d{1,3}[.]?\d*)/;
	var dmsPattern = /(\d{2})[^\d]*(\d{2})[^\d]*(\d{2}[.]?\d*)\s*([n|N|s|S])[^\w]*(\d{3})[^\d]*(\d{2})[^d]*(\d{2}[.]?\d*)\s*([e|E|w|W])/;
	var mgrsPattern = /(\d{1,2})([a-zA-Z])[^\w]*([a-zA-Z])([a-zA-Z])[^\w]*(\d{5})[^\w]*(\d{5})/;

	var coordinateConversion = new CoordinateConversion();

	if (inputString.match(dmsPattern)) {
		var latitude = coordinateConversion.dmsToDd(RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4);
		var longitude = coordinateConversion.dmsToDd(RegExp.$5, RegExp.$6, RegExp.$7, RegExp.$8);

		if (latitude && longitude) {
			latitude = parseFloat(latitude);
			longitude = parseFloat(longitude);
		}


		if (callbackFunction) { callbackFunction([longitude, latitude]); }
		else { return [longitude, latitude]; }
	}
	else if (inputString.match(ddPattern)) {
		var latitude = RegExp.$1;
		var longitude = RegExp.$2;

		if (latitude && longitude) {
			latitude = parseFloat(latitude);
			longitude = parseFloat(longitude);
		}


		if (callbackFunction) { callbackFunction([longitude, latitude]); }
		else { return [longitude, latitude]; }
	}
	else if (inputString.match(mgrsPattern)) {
		var location = coordinateConversion.mgrsToDd(RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4, RegExp.$5, RegExp.$6);


		convertGeospatialCoordinateFormat(location, callbackFunction);
	}
	else {
		if (callbackFunction) { callbackFunction(false); }
		else { return false; }
	}
}

function convertRadiusToBbox(x, y, radius) {
	/* radius * 1 nautical mile / 1852 meters * 1 minute latitude / 1 nautical mile * 1 deg latitude / 60 minute latitude */
	var deltaLatitude = radius / 1852 / 60;
	var deltaLongitude = radius / 1852 / 60 * Math.cos(Math.abs(y) * Math.PI / 180);


	return { maxLat: y + deltaLatitude, maxLon: x + deltaLongitude, minLat: y - deltaLatitude, minLon: x - deltaLongitude };
}

function disableMenuButtons() {
	var menuButtons = $(".navbar-header")[0].children;
	for (var i = 1; i < menuButtons.length - 1; i++) { $(menuButtons[i]).hide(); }

	var menuButtons = $(".navbar-nav")[0].children;
	for (var i = 1; i < menuButtons.length; i++) { $(menuButtons[i]).hide(); }
}

function displayDialog(dialog) {
	var header = $("#" + dialog + " .modal-header");
	var paddingHeight = header.offset().top;
	var headerHeight = header.outerHeight();
	var footerHeight = $("#" + dialog + " .modal-footer").outerHeight();

	var body = $("#" + dialog + " .modal-body");
	var maxBodyHeight = ($(window).height() - paddingHeight - headerHeight - footerHeight) * 0.9;
	var bodyIsTooTall = body.outerHeight() > maxBodyHeight;
	body.css("max-height", bodyIsTooTall ? maxBodyHeight : "");
	body.css("overflow-y", bodyIsTooTall ? "auto" : "");
}

function displayErrorDialog(message) {
	var messageDiv = $("#errorDialog").children()[1];
	$(messageDiv).html(message);
	$("#errorDialog").show();
}

function displayLoadingDialog(message) {
	$("#loadingDialog").modal("show");
	$("#loadingDialogMessageDiv").html(message);
}

function enableMenuButtons() {
	var menuButtons = $(".navbar-header")[0].children;
	for (var i = 1; i < menuButtons.length - 1; i++) { $(menuButtons[i]).show(); }

	var menuButtons = $(".navbar-nav")[0].children;
	for (var i = 1; i < menuButtons.length; i++) { $(menuButtons[i]).show(); }
}

function enableKeyboardShortcuts() {
	$(document).on(
		"keydown",
		function(event) {
			// only if a modal is not open
			if (!$(".modal-backdrop").is(":visible")) {
				var keyCode = event.keyCode;

				switch(keyCode) {
					// left arrow key
					case 37: changeFrame("rewind"); break;
					// right arrow key
					case 39: changeFrame("fastForward"); break;
					// delete key
					case 46: deleteFrame(); break;
				}
			}
		}
	);
}

function getGpsLocation(callbackFunction) {
	if (navigator.geolocation) {
		displayLoadingDialog("Don't worry, we'll find you... hopefully. #optimism");
		navigator.geolocation.getCurrentPosition(
			// success
			function(position) {
				hideLoadingDialog();
				callbackFunction(position);
			},
			// error
			function(error) {
				displayErrorDialog("Well, we tried to determine your location... and failed: " + error.message);
				hideLoadingDialog();
			}
		);
	}
	else { displayErrorDialog("Sorry, you're device doesn't support geolocation. :("); }
}

function hideDialog(dialog) {
	var dialogBody = $("#" + dialog + " .modal-body");
	dialogBody.css("max-height", "");
	dialogBody.css("overflow-y", "");
}

function hideErrorDialog() { $("#errorDialog").hide(); }

function hideLoadingDialog() { $("#loadingDialog").modal("hide"); }

function initializeLoadingDialog() {
	$("#loadingDialog").modal({
		keyboard: false,
		show: false
	});
}
