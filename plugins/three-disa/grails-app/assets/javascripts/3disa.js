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

function buildSourceSelectionTable() {
    var table = $( "#sourceSelectionTable" )[ 0 ];

    for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

    var row = table.insertRow( 0 );
    $( row ).css( "white-space", "nowrap" );
    var cell = row.insertCell( row.cells.length );
    var keys = [ "imageId", "acquisitionDate", "NIIRS", "grazingAngle", "elevationAngle", "CE", "LE" ];
    $.each(
        keys,
        function( i, x ) {
            var cell = row.insertCell( row.cells.length );
            $( cell ).append( x.capitalize().replace( /[a-z]([A-Z])/g, " $1" ) );
        }
    );

    $.each(
        tlv.layers,
        function( i, x ) {
            row = table.insertRow( table.rows.length );
            $( row ).click( function() {
                if ( $( this ).hasClass( "success" ) ) {
                    $( this ).removeClass( "success" );
                }
                else { $( this ).addClass( "success" ); }
            } );
            $( row ).css( "cursor", "pointer" );
            $( row ).mouseout( function() { $( this ).removeClass( "info" ); } );
            $( row ).mouseover( function() { $(  this ).addClass( "info" ); } );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( i + 1 );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.imageId );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.acquisitionDate + "z" );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.metadata.niirs );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.metadata.grazing_angle );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.metadata.elevation_angle );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.metadata.ce );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.metadata.le );

            cell = row.insertCell( row.cells.length );
            var span = document.createElement( "span" );
            span.className = "glyphicon glyphicon-trash";

            var deleteButton = document.createElement( "button" );
            deleteButton.className = "btn btn-primary btn-xs";
            deleteButton.onclick = function() {
                deleteFrame( i );
                buildSourceSelectionTable();
            };
            deleteButton.appendChild( span );
            $( cell ).append( deleteButton );
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

        groundToImagePoints( coordinates, newLayer, function( pixels, layer ) {
            $( "#" + currentLayer.map.getTarget() ).hide();

            var view = layer.map.getView();
            view.setCenter( [ pixels[ 0 ][ 0 ], -pixels[ 0 ][ 1 ] ] );
            view.setZoom( tlv[ "3disa" ].currentZoom );
            $( "#" + layer.map.getTarget() ).show();
        });
    });
}

function cleanup3Disa() {
    $.each( tlv[ "3disa" ].layers, function( index, layer ) { layer.map.setTarget( null ); } );
    $( "#tiePointMaps" ).html( "" );
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

function generateDem() {
    buildSourceSelectionTable();
    $( "#sourceSelectionDialog" ).modal( "show" );

    $( "#selectImagesButton" ).unbind();
    $( "#selectImagesButton" ).show();
    $( "#selectImagesButton" ).click( function() { setupTiePointSelectionDialog(); } );
}

function getSelectedImages() {
    var images = [];
    $.each(
        $( "#sourceSelectionTable tr" ),
        function( rowIndex, row ) {
            if ( $(row).hasClass( "success" ) ) { images.push( tlv.layers[ rowIndex - 1 ] ); }
        }
    );


    return images;
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

var pageLoad3Disa = pageLoad;
pageLoad = function() {
	pageLoad3Disa();

	tlv[ "3disa" ] = {};
}

function setupTiePointSelectionDialog() {
    cleanup3Disa();
    tlv[ "3disa" ].layers = [];
    tlv[ "3disa" ].currentLayer = 0;

    $("#tiePointSelectionDialog").modal( "show" );
    var selectedImages = getSelectedImages();
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
                })
            });

            $( "#tiePointMaps" ).append( "<div class = 'map' id = 'tiePointMap" + index + "'></div>" );
            tlv[ "3disa" ].layers[ index ].map = new ol.Map({
                layers: [ tlv[ "3disa" ].layers[ index ].mapLayer ],
                logo: false,
                target: "tiePointMap" + index,
                view: new ol.View({
                    center: [ imageWidth / 2, -imageHeight / 2 ],
                    extent: extent,
                    projection: new ol.proj.Projection({
                        code: "ImageSpace",
                        extent: [ 0, 0, imageWidth, imageHeight ],
                        units: "pixels"
                    }),
                    resolution: tlv.map.getView().getResolution()
                })
            });

            addTiePointVectorLayer( index );

            if ( index != 0 ) { $( "#tiePointMap" + index ).hide(); }
            else {
                var view = tlv.map.getView();
                var center = ol.proj.transform( view.getCenter(), "EPSG:3857", "EPSG:4326" );
                var extent = ol.proj.transform( view.calculateExtent( tlv.map.getSize() ), "EPSG:3857", "EPSG:4326" );
                var coordinates = [
                    center,
                    [ extent[ 0 ], center[ 1 ] ],
                    [ center[ 0 ], extent[ 1 ] ],
                    [ extent[ 2 ], center[ 1 ] ],
                    [ center[ 0 ], extent[ 3 ] ]
                ];

                groundToImagePoints( coordinates, tlv[ "3disa" ].layers[ 0 ], function( pixels, layer ) {
                    var center = [ pixels[ 0 ][ 0 ], -pixels[ 0 ][ 1 ] ];
                    var extent = [
                        pixels[ 1 ][ 0 ],
                        -pixels[ 2 ][ 1 ],
                        pixels[ 3 ][ 0 ],
                        -pixels[ 4 ][ 1 ]
                    ];

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
            "histOp=" + styles.hist_op,
            "resamplerFilter=" + styles.resampler_filter,
            "sharpenMOde=" + styles.sharpen_mode,
            "x=" + tileCoord[1],
            "y=" + ( -tileCoord[2] - 1 ),
            "z=" + tileCoord[0]
        ];


        return tlv.availableResources.complete[ image.library ].tileUrl + "?" + params.join( "&" );
    }
}
