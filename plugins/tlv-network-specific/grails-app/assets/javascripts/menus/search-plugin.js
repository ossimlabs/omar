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

var initializeLibraryCheckboxesPlugin = initializeLibraryCheckboxes;
initializeLibraryCheckboxes = function() {
	if (tlv.libraries) { initializeLibraryCheckboxesPlugin(); }
	else {
		var library = tlv.availableResources.libraries[0];
		var libraryCheckbox = $("#searchLibrary" + library.capitalize() + "Checkbox");
		libraryCheckbox.trigger("click");
	}
}
