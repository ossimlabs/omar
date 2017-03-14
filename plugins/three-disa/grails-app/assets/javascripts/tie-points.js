function aTiePointHasBeenAdded( event ) {
    var feature = event.feature;

    var numberOfFeatures = event.target.getFeatures().length;

    var style = createTiePointStyle();
    var textStyle = style.getText();
    textStyle.setText( numberOfFeatures.toString() );
    style.setText( textStyle );
    feature.setStyle( style );

    var pixel = feature.getGeometry().getCoordinates();
    var currentLayer = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ];
    currentLayer.map.removeInteraction( currentLayer.drawInteraction );
    imagePointsToGround( [[ pixel[ 0 ], -pixel[ 1 ] ]], currentLayer, function( coordinates, layer ) {
        $.each(
            tlv[ "3disa" ].layers,
            function( index, layer ) {
                var source = layer.vectorLayer.getSource();
                if ( source.getFeatures().length < numberOfFeatures ) {
                    // add a point in the same spot
                    groundToImagePoints( coordinates, layer, function( pixels, layer ) {
                        var newFeature = feature.clone();

                        var geometry = new ol.geom.Point( [ pixels[ 0 ][ 0 ], -pixels[ 0 ][ 1 ] ] );
                        newFeature.setGeometry( geometry );

                        var newStyle = newFeature.getStyle();
                        var newTextStyle = newStyle.getText();
                        newTextStyle.setText( numberOfFeatures.toString() );
                        newFeature.setStyle( newStyle );

                        layer.vectorLayer.getSource().addFeature( newFeature );
                    });
                }

                if ( index != tlv[ "3disa" ].currentLayer ) { layer.map.removeInteraction( layer.drawInteraction ); }
            }
        );
    });
}

function addTiePoint() {
    $.each(
        tlv[ "3disa" ].layers,
        function( index, layer ) {
            layer.map.addInteraction( layer.drawInteraction );
        }
    );
}

function addTiePointVectorLayer( index ) {
    var features = new ol.Collection();

    var source = new ol.source.Vector({ features: features });
    source.on( "addfeature", aTiePointHasBeenAdded );

    tlv[ "3disa" ].layers[ index ].vectorLayer = new ol.layer.Vector({ source: source });
    tlv[ "3disa" ].layers[ index ].map.addLayer( tlv[ "3disa" ].layers[ index ].vectorLayer );

    tlv[ "3disa" ].layers[ index ].drawInteraction = new ol.interaction.Draw({
        features: features,
        type: "Point"
    });

    var modifyInteraction = new ol.interaction.Modify({ features: features });
    tlv[ "3disa" ].layers[ index ].map.addInteraction( modifyInteraction );
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
    tlv[ "3disa" ].currentZoom = currentLayer.map.getView().getZoom();

    var center = currentLayer.map.getView().getCenter();
    imagePointsToGround( [[ center[0], -center[1] ]], currentLayer, function( coordinates, layer ) {
        if ( param == "fastForward" ) {
            tlv[ "3disa" ].currentLayer = tlv[ "3disa" ].currentLayer >= tlv[ "3disa" ].layers.length - 1 ? 0 : tlv[ "3disa" ].currentLayer + 1;
        }
        else {
            tlv[ "3disa" ].currentLayer = tlv[ "3disa" ].currentLayer <= 0 ? tlv[ "3disa" ].layers.length - 1 : tlv[ "3disa" ].currentLayer - 1;
        }
        var newLayer = tlv[ "3disa" ].layers[ tlv[ "3disa" ].currentLayer ];

        newLayer.mapLayer.setVisible( true );
        groundToImagePoints( coordinates, newLayer, function( pixels, layer ) {
            $( "#" + currentLayer.map.getTarget() ).hide();

            var view = layer.map.getView();
            view.setCenter( [ pixels[ 0 ][ 0 ], -pixels[ 0 ][ 1 ] ] );
            view.setZoom( tlv[ "3disa" ].currentZoom );
            $( "#" + layer.map.getTarget() ).show();
        });
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

function getNorthAndUpAngles( layer ) {
    $.ajax({
        data: "entry=0&filename=" + layer.metadata.filename,
        dataType: "json",
        success: function( data ) {
            layer.northAngle = data.northAngle;
            layer.upAngle = data.upAngle;

            rotateNorthArrow( layer, layer.northAngle );
            layer.map.getView().setRotation( layer.upAngle );
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
            "pqeIncludePositionError": false,
            "pqeProbabilityLevel" : 0.9,
        }),
        dataType: "json",
        success: function( data ) {
            var coordinates = data.data.map(
                function( point ) { return [ point.lon, point.lat ]; }
            );
            callback( coordinates, layer );
        },
        type: "post",
        url: tlv.availableResources.complete[ layer.library ].mensaUrl + "/imagePointsToGround"
    });
}

function rotateNorthArrow( layer, radians ) {
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

    if ( !$( "#sensorModelSelect" ).val() ) {
        displayErrorDialog( "Ummm... you're going to need to select a sensor model." );
        return;
    }

    $( "#sourceSelectionDialog" ).modal( "hide" );
    $("#tiePointSelectionDialog").modal( "show" );

    tlv[ "3disa" ].layers = [];
    tlv[ "3disa" ].currentLayer = 0;
    $.each(
        selectedImages,
        function( index, layer ) {
            tlv[ "3disa" ].layers.push({
                acquisitionDate: layer.acquisitionDate,
                imageId: layer.imageId,
                library: layer.library,
                metadata: layer.metadata
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

            $( "#tiePointMaps" ).append( "<div class = 'map' id = 'tiePointMap" + index + "'></div>" );
            var view = new ol.View({
                center: [ imageWidth / 2, -imageHeight / 2 ],
                extent: extent,
                projection: new ol.proj.Projection({
                    code: "ImageSpace",
                    extent: [ 0, 0, imageWidth, imageHeight ],
                    units: "pixels"
                }),
                resolution: tlv.map.getView().getResolution()
            });
            view.on('change:rotation', function( event ) {
                if ( layer.northAngle ) {
                    var rotation = event.target.get( event.key ) - layer.northAngle;
                    rotateNorthArrow( layer, rotation );
                }
            });

            tlv[ "3disa" ].layers[ index ].map = new ol.Map({
                layers: [ tlv[ "3disa" ].layers[ index ].mapLayer ],
                logo: false,
                target: "tiePointMap" + index,
                view: view
            });

            getNorthAndUpAngles( tlv[ "3disa" ].layers[ index ] );

            addTiePointVectorLayer( index );

            if ( index != 0 ) { $( "#tiePointMap" + index ).hide(); }
            else {
                var view = tlv.map.getView();
                var center = ol.proj.transform( view.getCenter(), "EPSG:3857", "EPSG:4326" );

                groundToImagePoints( [ center ], tlv[ "3disa" ].layers[ 0 ], function( pixels, layer ) {
                    var center = [ pixels[ 0 ][ 0 ], -pixels[ 0 ][ 1 ] ];
                    tlv[ "3disa" ].layers[ 0 ].map.getView().setCenter( center );
                });
            }
        }
    );
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
            "histCenterTile=" + styles[ "histogram-center-tile" ],
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
