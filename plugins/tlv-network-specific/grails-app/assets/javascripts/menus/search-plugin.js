var beginSearchPlugin = beginSearch;
beginSearch = function() {
	var location = getLocation();
	if (location) { beginSearchPlugin(); }
	else {
		var locationString = $("#searchLocationInput").val() != "" ? $("#searchLocationInput").val() : tlv.defaultLocation;
		var callbackFunction = function(point) {
			var getLocationPlugin = getLocation;
			getLocation = function() { return point; }
			beginSearchPlugin();
			getLocation = getLocationPlugin;
		}
		var location = convertGeospatialCoordinateFormat(locationString, callbackFunction);
	}
}
