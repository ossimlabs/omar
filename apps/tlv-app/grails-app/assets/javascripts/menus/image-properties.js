var changeFrameImageProperties = changeFrame;
changeFrame = function( params ) {
	changeFrameImageProperties( params );

	syncImageProperties();
}

var pageLoadImageProperties = pageLoad;
pageLoad = function() {
	pageLoadImageProperties();

	var brightnessSlider = $( "#brightnessSliderInput" );
	brightnessSlider.slider({
		max: 100,
		min: -100,
		tooltip: "hide",
		value: 0
	});
	brightnessSlider.on("change", function( event ) {
		$( "#brightnessValueSpan" ).html( ( event.value.newValue / 100 ).toFixed( 2 ) );
	});

	var contrastSlider = $( "#contrastSliderInput" );
	contrastSlider.slider({
		max: 100,
		min: -100,
		tooltip: "hide",
		value: 100
	});
	contrastSlider.on("change", function( event ) {
		$( "#contrastValueSpan" ).html( ( event.value.newValue / 100 ).toFixed( 2 ) );
	});
}

function selectBands( selectionMethod ) {
	if ( selectionMethod == "manual" ) { $( "#manualBandSelectTable" ).show(); }
	else { $( "#manualBandSelectTable" ).hide(); }

	updateImageProperties();
}

var setupTimeLapseImageProperties = setupTimeLapse;
setupTimeLapse = function() {
	setupTimeLapseImageProperties();

	syncImageProperties();
}

function syncImageProperties() {
	var layer = tlv.layers[ tlv.currentLayer ];
	var metadata = layer.metadata;
	var styles = JSON.parse( layer.mapLayer.getSource().getParams().STYLES );


	$.each(
		[ "red", "green", "blue" ],
		function( i, x ) {
			var select = $( "#" + x + "GunSelect" );
			select.html("");
			for ( var bandNumber = 1; bandNumber <= metadata.numberOfBands; bandNumber++) {
				select.append( "<option value = " + bandNumber + " >" + bandNumber + "</option>");
			}
		}
	);
	if ( styles.bands == "default" ) {
		$( "#selectBandsMethodSelect option[value='default']" ).prop( "selected", true );
		$( "#manualBandSelectTable" ).hide();
	}
	else {
		$( "#selectBandsMethodSelect option[value='manual']" ).prop( "selected", true );
		$( "#manualBandSelectTable" ).show();
		var bands = styles.bands.split(",");
		$.each(
			[ "red", "green", "blue" ],
			function( i, x ) {
				$( "#" + x + "GunSelect option[value=" + bands[i] + "]" ).prop( "selected", true );
			}
		);
	}

	$( "#brightnessSliderInput" ).slider( "setValue", styles.brightness * 100 );
	$( "#brightnessValueSpan" ).html(( styles.brightness ));

	$( "#contrastSliderInput" ).slider( "setValue", styles.contrast * 100 );
	$( "#contrastValueSpan" ).html(( styles.contrast ));

	$( "#dynamicRangeSelect option[value='" + styles.hist_op + "']" ).prop( "selected", true );
	$( "#interpolationSelect option[value='" + styles.resample_filter + "']" ).prop( "selected", true );
	$( "#sharpenModeSelect option[value='" + styles.sharpen_mode + "']" ).prop( "selected", true );
}

function updateImageProperties() {
	var bands = $( "#selectBandsMethodSelect" ).val();
 	if ( bands != "default" ) {
		var red = $( "#redGunSelect" ).val();
		var green = $( "#greenGunSelect" ).val();
		var blue = $( "#blueGunSelect" ).val();
		bands = [ red, green, blue ].join( "," );
	}

	tlv.layers[tlv.currentLayer].mapLayer.getSource().updateParams({
		STYLES: JSON.stringify({
			bands: bands,
			brightness: $( "#brightnessSliderInput" ).slider( "getValue" ) / 100,
			contrast: $( "#contrastSliderInput" ).slider( "getValue" ) / 100,
			hist_op: $( "#dynamicRangeSelect" ).val(),
			resampler_filter: $( "#interpolationSelect" ).val(),
			sharpen_mode: $( "#sharpenModeSelect" ).val()
		})
	});
	tlv.map.renderSync();
}
