function buildSourceSelectionTable() {
    var table = $( "#sourceSelectionTable" )[ 0 ];

    for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

    var row = table.insertRow( 0 );
    $( row ).css( "white-space", "nowrap" );
    var cell = row.insertCell( row.cells.length );
    $.each(
        [ "Image ID", "Acquisition Date", "NIIRS", "Grazing Angle", "Elevation Angle", "CE", "LE" ],
        function( index, value ) {
            var cell = row.insertCell( row.cells.length );
            $( cell ).append( value );
        }
    );

    $.each(
        tlv.layers,
        function( i, x ) {
            row = table.insertRow( table.rows.length );
            $( row ).addClass( "success" );
            $( row ).click( function() {
                if ( $( this ).hasClass( "success" ) ) { $( this ).removeClass( "success" ); }
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
