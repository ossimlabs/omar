function aTiePointHasBeenAdded( event ) {
    var feature = event.feature;

    var numberOfFeatures = event.target.getFeatures().length;

    var featureId = Date.parse( new Date() );
    feature.setProperties({ id: featureId });

    var style = createTiePointStyle();
    var textStyle = style.getText();
    textStyle.setText( numberOfFeatures.toString() );
    style.setText( textStyle );
    feature.setStyle( style );

    var pixel = feature.getGeometry().getCoordinates();
    var currentLayer = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ];
    tlv[ "3disa" ].map.removeInteraction( currentLayer.drawInteraction );
    imagePointsToGround( [[ pixel[ 0 ], -pixel[ 1 ] ]], currentLayer, function( coordinates, layer ) {
        $.each(
            tlv[ "3disa" ].layers,
            function( index, layer ) {
                var source = layer.vectorLayer.getSource();
                if ( source.getFeatures().length < numberOfFeatures ) {
                    // add a point in the same spot
                    groundToImagePoints( coordinates, layer, function( pixels, layer ) {
                        var newFeature = feature.clone();

                        newFeature.setProperties({ id: featureId });

                        var geometry = new ol.geom.Point( [ pixels[ 0 ][ 0 ], -pixels[ 0 ][ 1 ] ] );
                        newFeature.setGeometry( geometry );

                        var newStyle = newFeature.getStyle();
                        var newTextStyle = newStyle.getText();
                        newTextStyle.setText( numberOfFeatures.toString() );
                        newFeature.setStyle( newStyle );

                        layer.vectorLayer.getSource().addFeature( newFeature );
                    });
                }
            }
        );
    });
}

function addTiePoint() {
    tlv[ "3disa" ].map.addInteraction( tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ].drawInteraction );
}

function addTiePointVectorLayer( index ) {
    var features = new ol.Collection();
    var map = tlv[ "3disa" ].map;

    tlv[ "3disa" ].layers[ index ].vectorLayer = new ol.layer.Vector({
        source: new ol.source.Vector({ features: features }),
        visible: false
    });
    tlv[ "3disa" ].layers[ index ].vectorLayer.getSource().on( "addfeature", aTiePointHasBeenAdded );
    map.addLayer( tlv[ "3disa" ].layers[ index ].vectorLayer );

    tlv[ "3disa" ].layers[ index ].drawInteraction = new ol.interaction.Draw({
        features: features,
        type: "Point"
    });

    var modifyInteraction = new ol.interaction.Modify({ features: features });
    map.addInteraction( modifyInteraction );

    map.getViewport().addEventListener( "contextmenu",
        function ( event ) {
            event.preventDefault();
            var pixel = [ event.layerX, event.layerY ];
            var callback = function( feature ) {
                // check for an id to make sure it's not the select faux feature
                if ( feature.getProperties().id ) { deleteTiePoint( feature ); }
            }
            map.forEachFeatureAtPixel( pixel, callback );
        }
    );
}

function calculateTileResolutions( imageHeight, imageWidth ) {
    var tileSize = ol.DEFAULT_TILE_SIZE || 256;
    var tierSizeInTiles = [];
    while ( imageWidth > tileSize || imageHeight > tileSize ) {
        tierSizeInTiles.push([
            Math.ceil( imageWidth / tileSize ),
            Math.ceil( imageHeight / tileSize )
        ]);
        tileSize += tileSize;
    }
    tierSizeInTiles.push([1, 1]);
    tierSizeInTiles.reverse();

    var resolutions = [1];
    var tileCountUpToTier = [0];
    var i = 1, ii = tierSizeInTiles.length;
    while ( i < ii ) {
        resolutions.push( 1 << i );
        tileCountUpToTier.push(
            tierSizeInTiles[ i - 1 ][ 0 ] * tierSizeInTiles[ i - 1 ][ 1 ] + tileCountUpToTier[ i - 1 ]
        );
        i++;
    }


    return resolutions.reverse();
}

function changeTiePointFrame( param ) {
    var currentLayer = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ];

    currentLayer.mapLayer.setVisible( false );
    currentLayer.vectorLayer.setVisible( false );


    var view = tlv[ "3disa" ].map.getView();

    var rotation = currentLayer.rotation;
    view.setRotation( 0 );
    currentLayer.rotation= rotation;

    var center = view.getCenter();
    var extent = view.calculateExtent( tlv[ "3disa" ].map.getSize() );
    var coordinates = [
        [ center[ 0 ], -center[ 1 ] ],
        [ extent[ 0 ], -extent[ 1 ] ],
        [ extent[ 2 ], -extent[ 3 ] ]
    ];
    imagePointsToGround( coordinates, currentLayer, function( coordinates, layer ) {
        if ( param == "fastForward" ) {
            tlv[ "3disa" ].currentLayer = tlv[ "3disa" ].currentLayer >= tlv[ "3disa" ].layers.length - 1 ? 0 : tlv[ "3disa" ].currentLayer + 1;
        }
        else {
            tlv[ "3disa" ].currentLayer = tlv[ "3disa" ].currentLayer <= 0 ? tlv[ "3disa" ].layers.length - 1 : tlv[ "3disa" ].currentLayer - 1;
        }
        var newLayer = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ];

        groundToImagePoints( coordinates, newLayer, function( pixels, layer ) {


            var view = tlv[ "3disa" ].map.getView();

            var center = [ pixels[ 0 ], -pixels[ 1 ] ];
            view.setCenter( center );

            var extent = [ pixels[ 1 ][ 0 ], -pixels[ 1 ][ 1 ], pixels[ 2 ][ 0 ], -pixels[ 2 ][ 1 ] ];
            view.fit( extent, tlv[ "3disa" ].map.getSize(), { nearest: true } );

            view.setRotation( layer.rotation );

            layer.mapLayer.setVisible( true );
            layer.vectorLayer.setVisible( true );

            updateTiePointScreenText();
        });
    });
}

function createTiePointMap( images ) {
    var maxHeight = Math.max.apply( null,  images.map( function( image ) { return image.metadata.height; } ) );
    var maxWidth = Math.max.apply( null, images.map( function( image ) { return image.metadata.width; } ) );
    tlv[ "3disa" ].map = new ol.Map({
        logo: false,
        target: "tiePointMap",
        view: new ol.View({
            center: [ maxWidth / 2, -maxHeight / 2 ],
            extent: [ 0, -maxHeight, maxWidth, 0 ],
            projection: new ol.proj.Projection({
                code: "ImageSpace",
                extent: [ 0, 0, maxWidth, maxHeight ],
                units: "pixels"
            }),
            resolution: tlv.map.getView().getResolution()
        })
    });

    tlv[ "3disa" ].map.getView().on('change:rotation', function( event ) {
        var rotation = event.target.get( event.key );
        tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ].rotation = rotation;
        rotateNorthArrow( rotation );
    });
}

function createTiePointStyle() {
    return new ol.style.Style({
        image: new ol.style.Circle({
            fill: new ol.style.Fill({ color: "rgba(255, 255, 0, 1)" }),
            radius: 5
        }),
        text: new ol.style.Text({
            fill: new ol.style.Fill({ color: "rgba(255, 255, 0, 1)" }),
            offsetY: 20,
            textBaseline: "bottom"
        })
    });
}

function deleteTiePoint( feature ) {
    var id = feature.getProperties().id;
    $.each(
        tlv[ "3disa" ].layers,
        function( index, layer ) {
            var source = layer.vectorLayer.getSource();
            $.each(
                source.getFeatures(),
                function( index, feature ) {
                    if ( feature && feature.getProperties().id == id ) { source.removeFeature( feature ); }
                }
            );
        }
    );

    $.each(
        tlv[ "3disa" ].layers,
        function( index, layer ) {
            var features = layer.vectorLayer.getSource().getFeatures().sort(
                function( a, b ) {
                    var aId = a.getProperties().id;
                    var bId = b.getProperties().id;


                    return  aId > bId ? 1 : ( bId > aId ? -1 : 0 );
                }
            );
            $.each(
                features,
                function( index, feature ) {
                    var style = feature.getStyle();
                    var textStyle = style.getText();
                    textStyle.setText( ( index + 1 ).toString() );
                    style.setText( textStyle );
                    feature.setStyle( style );
                }
            );
        }
    );
}

function getNorthAndUpAngles( layer ) {
    $.ajax({
        data: "entry=0&filename=" + layer.metadata.filename,
        dataType: "json",
        success: function( data ) {
            layer.northAngle = data.northAngle;
            layer.upAngle = data.upAngle;

            rotateNorthArrow( layer.northAngle );
            tlv[ "3disa" ].map.getView().setRotation( layer.upAngle );
        },
        url: tlv.availableResources.complete[ layer.library ].imageSpaceUrl + "/getAngles"
    });
}

function groundToImagePoints( coordinates, layer, callback ) {
    $.ajax({
        contentType: "application/json",
        data: JSON.stringify({
            "entryId": 0,
            "filename": layer.metadata.filename,
            "pointList": coordinates.map(
                function( coordinate ) { return { "lat": coordinate[1], "lon": coordinate[0] }; }
            ),
        }),
        dataType: "json",
        success: function( data ) {
            var pixels = data.data.map(
                function( point ) { return [ point.x, point.y ] }
            );
            callback( pixels, layer );
        },
        type: "post",
        url: tlv.availableResources.complete[ layer.library ].mensaUrl + "/groundToImagePoints"
    });
}

function imagePointsToGround( pixels, layer, callback ) {
    $.ajax({
        contentType: "application/json",
        data: JSON.stringify({
            "entryId": 0,
            "filename": layer.metadata.filename,
            "pointList": pixels.map(
                function( pixel ) { return { "x": pixel[0], "y": pixel[1] }; }
            ),
            "pqeEllipseAngularIncrement": 10,
            "pqeEllipsePointType" : "none",
            "pqeIncludePositionError": true,
            "pqeProbabilityLevel" : 0.9,
        }),
        dataType: "json",
        success: function( data ) {
            var coordinates = data.data.map(
                function( point ) { return [ point.lon, point.lat ]; }
            );
            var errors = data.data.map(
                function( point ) {
                    if ( point.pqe.pqeValid ) { return { CE: point.pqe.CE, LE: point.pqe.LE } }
                    else { return { CE: null, LE: null } }
                }
            );
            callback( coordinates, layer, errors );
        },
        type: "post",
        url: tlv.availableResources.complete[ layer.library ].mensaUrl + "/imagePointsToGround"
    });
}

function rotateNorthArrow( radians ) {
    var layer = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ];
    if ( layer.northAngle ) { radians = radians - layer.northAngle; }

    var transform = 'rotate(' + radians + 'rad)';
    var arrow = $('.ol-compass');
    arrow.css('msTransform', transform);
    arrow.css('transform', transform);
    arrow.css('webkitTransform', transform);
}

function setupTiePointSelectionDialog() {
    var selectedImages = getSelectedImages();
    if ( selectedImages.length < 2 ) {
        displayErrorDialog( "It takes at least two images to Tango." );
        return;
    }

    $( "#sourceSelectionDialog" ).modal( "hide" );
    $( "#tiePointSelectionDialog" ).modal( "show" );

    tlv[ "3disa" ].layers = [];
    tlv[ "3disa" ].currentLayer = 0;

    createTiePointMap( selectedImages );

    $.each(
        selectedImages,
        function( index, layer ) {
            tlv[ "3disa" ].layers.push({
                acquisitionDate: layer.acquisitionDate,
                imageId: layer.imageId,
                library: layer.library,
                metadata: layer.metadata,
                rotation: 0,
                sensorModel: layer.sensorModel
            });

            var filename = layer.metadata.filename;
            var imageHeight = layer.metadata.height;
            var imageWidth = layer.metadata.width;

            var extent = [ 0, -imageHeight, imageWidth, 0 ];
            tlv[ "3disa" ].layers[ index ].mapLayer = new ol.layer.Tile({
                source: new ol.source.TileImage({
                    tileClass: ol.source.ZoomifyTile,
                    tileGrid: new ol.tilegrid.TileGrid({
                        extent: extent,
                        origin: ol.extent.getTopLeft( extent ),
                        resolutions: calculateTileResolutions( imageHeight, imageWidth )
                    }),
                    tileUrlFunction: function( tileCoord, pixelRatio, projection ) {


                        return tileUrlFunction( layer, tileCoord, pixelRatio, projection )
                    }
                }),
                visible: false
            });

            tlv[ "3disa" ].map.addLayer( tlv[ "3disa" ].layers[ index ].mapLayer );
            addTiePointVectorLayer( index );

            getNorthAndUpAngles( tlv[ "3disa" ].layers[ index ] );
        }
    );

    var view = tlv.map.getView();
    var center = ol.proj.transform( view.getCenter(), "EPSG:3857", "EPSG:4326" );
    var extent = ol.proj.transformExtent( view.calculateExtent( tlv.map.getSize() ), "EPSG:3857", "EPSG:4326" );
    var coordinates = [ center, extent.slice( 0, 2 ), extent.slice( 2, 4 ) ];
    groundToImagePoints( coordinates, tlv[ "3disa" ].layers[ 0 ], function( pixels, layer ) {
        var view = tlv[ "3disa" ].map.getView();

        var center = [ pixels[ 0 ][ 0 ], -pixels[ 0 ][ 1 ] ];
        view.setCenter( center );

        var extent = [ pixels[ 1 ][ 0 ], -pixels[ 1 ][ 1 ], pixels[ 2 ][ 0 ], -pixels[ 2 ][ 1 ] ];
        view.fit( extent, tlv[ "3disa" ].map.getSize(), { nearest: true } );

        tlv[ "3disa" ].layers[ 0 ].mapLayer.setVisible( true );
        tlv[ "3disa" ].layers[ 0 ].vectorLayer.setVisible( true );
    });

    updateTiePointScreenText();
}

function tileUrlFunction( image, tileCoord, pixelRatio, projection ) {
    if ( !tileCoord ) { return undefined; }
    else {
        var styles = JSON.parse( image.mapLayer.getSource().getParams().STYLES );
        var params = [
            "bands=" + styles.bands,
            "brightness=" + styles.brightness,
            "contrast=" + styles.contrast,
            "entry=0&",
            "filename=" + image.metadata.filename,
            "format=jpeg&",
            "histCenterTile=" + styles[ "hist_center" ],
            "histOp=" + styles.hist_op,
            "resamplerFilter=" + styles.resampler_filter,
            "sharpenMOde=" + styles.sharpen_mode,
            "x=" + tileCoord[1],
            "y=" + ( -tileCoord[2] - 1 ),
            "z=" + tileCoord[0]
        ];


        return tlv.availableResources.complete[ image.library ].imageSpaceUrl + "/getTile?" + params.join( "&" );
    }
}

function updateTiePointAcquisitionDate() {
	var acquisitionDate = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ].acquisitionDate;
	if ( acquisitionDate ) {
		var timeToNextImage = getTimeToAdjacentImage( tlv[ "3disa" ].layers, tlv[ "3disa" ].currentLayer, "next");
		var timeToPreviousImage = getTimeToAdjacentImage( tlv[ "3disa" ].layers, tlv[ "3disa" ].currentLayer, "previous" );
		$( "#tiePointAcquisitionDateDiv" ).html(
			(timeToPreviousImage ? timeToPreviousImage + " <- " : "") +
			acquisitionDate + (acquisitionDate != "N/A" ? "z" : "") +
			(timeToNextImage ? " -> " + timeToNextImage : "")
		);
	}
	else { $( "#tiePointAcquisitionDateDiv" ).html("N/A"); }
}

function updateTiePointImageId() {
	var layer = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ];
	var libraryLabel = tlv.availableResources.complete[ layer.library ].label;
	$("#tiePointImageIdDiv").html( libraryLabel + ": " + layer.imageId );
}

function updateTiePointScreenText() {
	updateTiePointImageId();
	updateTiePointAcquisitionDate();
	updateTiePointLayerCount();
}

function updateTiePointLayerCount() {
	var currentCount = tlv[ "3disa" ].currentLayer + 1;
	$( "#tiePointImageCountDiv" ).html( currentCount + "/" + tlv[ "3disa" ].layers.length );
}
