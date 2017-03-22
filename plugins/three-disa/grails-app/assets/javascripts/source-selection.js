function buildSourceSelectionTable() {
    var table = $( "#sourceSelectionTable" )[ 0 ];

    for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

    var row = table.insertRow( 0 );
    $( row ).css( "white-space", "nowrap" );
    $.each(
        [ "Use", "", "Image ID", "Sensor Model", "NIIRS", "Azimuth", "Graze", "CE (m<sup>2</sup>)", "LE (m)" ],
        function( index, value ) {
            var cell = row.insertCell( row.cells.length );
            $( cell ).append( value );
        }
    );

    $.each(
        tlv.layers,
        function( index, layer ) {
            row = table.insertRow( table.rows.length );
            $( row ).addClass( "success" );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( "<input checked type = 'checkbox'>" );
            $( $( cell ).children() ).click( function() {
                var row = $( this ).parent().parent();
                if ( !$( this ).is( ":checked" ) ) { $( row ).removeClass( "success" ); }
                else { $( row ).addClass( "success" ); }
            });

            cell = row.insertCell( row.cells.length );
            $( cell ).append( index + 1 );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( layer.imageId );

            cell = row.insertCell( row.cells.length );
            $( cell ).append(
                "<select class = 'form-control' id = 'sensorModelSelect" + index + "'>" +
                    "<option value = 'absoluteRanking'>Absolute Ranking</option>" +
                    "<option value = 'rigorous'>Rigorous</option>" +
                "</select>"
            );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( layer.metadata.niirs );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( layer.metadata.azimuth_angle ? layer.metadata.azimuth_angle.toFixed( 2 ) : "" );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( layer.metadata.grazing_angle ? layer.metadata.grazing_angle.toFixed( 2 ) : "" );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( layer.CE );
            $( cell ).attr( "id", layer.metadata.index_id + "CE" );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( layer.LE );
            $( cell ).attr( "id", layer.metadata.index_id + "LE" );

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

    getErrorValues();
}

function getErrorValues() {
    var center = ol.proj.transform( tlv.map.getView().getCenter(), "EPSG:3857", "EPSG:4326" );
    $.each(
        tlv.layers,
        function( index, layer ) {
                var callback = function( pixels, layer  ) {
                    var callback2 = function( coordinates, layer, errors ) {
                        layer.CE = errors[0].CE;
                        $( "#" + layer.metadata.index_id + "CE" ).html(
                            isNaN( parseFloat( layer.CE ) ) ? "N/A" : layer.CE.toFixed( 2 )
                        );
                        layer.LE = errors[0].LE;
                        $( "#" + layer.metadata.index_id + "LE" ).html(
                            isNaN( parseFloat( layer.LE ) ) ? "N/A" : layer.LE.toFixed( 2 )
                        );
                    }
                    imagePointsToGround( pixels, layer, callback2 );
                }
                groundToImagePoints( [ center ], layer, callback );
        }
    );
}

function getSelectedImages() {
    var images = [];
    $.each(
        $( "#sourceSelectionTable tr" ),
        function( index, row ) {
            if ( $( row ).hasClass( "success" ) ) {
                var sensorModel = $( "#sensorModelSelect" + ( index - 1 ) ).val();
                tlv.layers[ index - 1 ].sensorModel = sensorModel;
                images.push( tlv.layers[ index - 1 ] );
            }
        }
    );


    return images;
}
