var convertGeospatialCoordinateFormatPlugin = convertGeospatialCoordinateFormat;
convertGeospatialCoordinateFormat = function( inputString, callbackFunction ) {
	var location = convertGeospatialCoordinateFormatPlugin( inputString );

	var bePattern = /\d{4}[a-z|\-{1}][a-z|0-9]\d{4}/i;

	if ( location ) {
		if ( callbackFunction ) { callbackFunction( location ); }
		else { return location; }
	}
	else if ( callbackFunction ) {
		displayLoadingDialog( "We're checking our maps for that location... BRB!" );
		if ( inputString.match( bePattern ) ) {
			$.ajax({
				data: "beNumber=" + inputString,
				dataType: "json",
				error: function( jqXhr, textStatus, errorThrown ) {
					hideLoadingDialog();
				},
				success: function( data ) {
					hideLoadingDialog();
					if ( data.length == 0 ) { displayErrorDialog( "We couldn't find that BE. :(" ); }
					else {
						var point = data;
						callbackFunction( point );
					}
				},
				url: tlv.contextPath + "/be"
			});
		}
		// assume it's a placename
		else {
			$.ajax({
				data: "location=" + inputString,
				dataType: "json",
				error: function( jqXhr, textStatus, errorThrown ) {
					hideLoadingDialog();
				},
				success: function( data ) {
					hideLoadingDialog();
					if ( data.length == 0 ) { displayErrorDialog( "We couldn't find that location. :(" ); }
					else {
						var point = data;
						callbackFunction( point );
					}
				},
				url: tlv.contextPath + "/geocoder"
			});
		}
	}
	else { return false; }
}
